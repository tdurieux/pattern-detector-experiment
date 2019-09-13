/**
 *
 */
package edu.washington.escience.myria.perfenforce;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.core.Context;

import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.washington.escience.myria.MyriaConstants;
import edu.washington.escience.myria.RelationKey;
import edu.washington.escience.myria.Schema;
import edu.washington.escience.myria.Type;
import edu.washington.escience.myria.operator.DbInsert;
import edu.washington.escience.myria.operator.DbQueryScan;
import edu.washington.escience.myria.operator.EOSSource;
import edu.washington.escience.myria.operator.RootOperator;
import edu.washington.escience.myria.operator.SinkRoot;
import edu.washington.escience.myria.operator.network.GenericShuffleConsumer;
import edu.washington.escience.myria.operator.network.GenericShuffleProducer;
import edu.washington.escience.myria.operator.network.partition.FixValuePartitionFunction;
import edu.washington.escience.myria.operator.network.partition.RoundRobinPartitionFunction;
import edu.washington.escience.myria.parallel.ExchangePairID;
import edu.washington.escience.myria.parallel.Server;
import edu.washington.escience.myria.perfenforce.encoding.StatsTableEncoding;
import edu.washington.escience.myria.perfenforce.encoding.TableDescriptionEncoding;
import edu.washington.escience.myria.util.MyriaUtils;

/**
 * Methods to help prepare the data for PSLA generation
 */
public class PerfEnforceDataPreparation {

  @Context private Server server;

  private HashMap<Integer, RelationKey> factTableRelationMapper;
  private TableDescriptionEncoding factTableDescription;

  /** Logger. */
  protected static final org.slf4j.Logger LOGGER =
      LoggerFactory.getLogger(PerfEnforceDataPreparation.class);

  /*
   * Ingesting the fact table in a parallel sequence
   */
  public HashMap<Integer, RelationKey> ingestFact(final TableDescriptionEncoding factTableDesc) {
    factTableRelationMapper = new HashMap<Integer, RelationKey>();

    ArrayList<RelationKey> relationKeysToUnion = new ArrayList<RelationKey>();
    Collections.sort(PerfEnforceDriver.configurations, Collections.reverseOrder());

    // Create a sequence for the largest cluster size
    int maxConfig = PerfEnforceDriver.configurations.get(0);
    Set<Integer> maxWorkerRange = PerfEnforceUtils.getWorkerRangeSet(maxConfig);

    /*
     * First, ingest the fact table under the relationKey with the union ("_U"). Then, create a materialized view with
     * the original relationKey name and add it to the catalog. This is what the user will be using on the MyriaL front
     * end.
     */
    RelationKey relationKeyWithUnion =
        new RelationKey(
            factTableDesc.relationKey.getUserName(),
            factTableDesc.relationKey.getProgramName(),
            factTableDesc.relationKey.getRelationName() + maxConfig + "_U");
    server.parallelIngestDataset(
        relationKeyWithUnion,
        factTableDesc.schema,
        factTableDesc.delimiter,
        null,
        null,
        null,
        factTableDesc.source,
        maxWorkerRange);
    relationKeysToUnion.add(relationKeyWithUnion);

    RelationKey relationKeyOriginal =
        new RelationKey(
            factTableDesc.relationKey.getUserName(),
            factTableDesc.relationKey.getProgramName(),
            factTableDesc.relationKey.getRelationName() + maxConfig);
    server.createMaterializedView(
        relationKeyOriginal.toString(MyriaConstants.STORAGE_SYSTEM_POSTGRESQL),
        PerfEnforceUtils.createUnionQuery(relationKeysToUnion),
        maxWorkerRange);
    server.addDatasetToCatalog(relationKeyOriginal, factTableDesc.schema, maxWorkerRange);
    factTableRelationMapper.put(maxConfig, relationKeyOriginal);

    /*
     * Iterate and run this for the rest of the workers
     */
    Set<Integer> previousWorkerRange = maxWorkerRange;
    RelationKey previousRelationKey = relationKeyWithUnion;
    for (int c = 1; c < PerfEnforceDriver.configurations.size(); c++) {
      // Get the next sequence of workers
      int currentSize = PerfEnforceDriver.configurations.get(c);
      Set<Integer> currentWorkerRange = PerfEnforceUtils.getWorkerRangeSet(currentSize);
      Set<Integer> diff =
          com.google.common.collect.Sets.difference(previousWorkerRange, currentWorkerRange);

      RelationKey currentRelationKeyToUnion =
          new RelationKey(
              factTableDesc.relationKey.getUserName(),
              factTableDesc.relationKey.getProgramName(),
              factTableDesc.relationKey.getRelationName() + currentSize + "_U");

      final ExchangePairID shuffleId = ExchangePairID.newID();
      DbQueryScan scan = new DbQueryScan(previousRelationKey, factTableDesc.schema);

      int[] producingWorkers =
          PerfEnforceUtils.getRangeInclusiveArray(Collections.min(diff), Collections.max(diff));
      int[] receivingWorkers =
          PerfEnforceUtils.getRangeInclusiveArray(1, Collections.max(currentWorkerRange));

      GenericShuffleProducer producer =
          new GenericShuffleProducer(
              scan,
              shuffleId,
              receivingWorkers,
              new RoundRobinPartitionFunction(receivingWorkers.length));
      GenericShuffleConsumer consumer =
          new GenericShuffleConsumer(factTableDesc.schema, shuffleId, producingWorkers);
      DbInsert insert = new DbInsert(consumer, currentRelationKeyToUnion, true);

      Map<Integer, RootOperator[]> workerPlans = new HashMap<>(currentSize);
      for (Integer workerID : producingWorkers) {
        workerPlans.put(workerID, new RootOperator[] {producer});
      }
      for (Integer workerID : receivingWorkers) {
        workerPlans.put(workerID, new RootOperator[] {insert});
      }

      server.submitQueryPlan(new SinkRoot(new EOSSource()), workerPlans).get();
      relationKeysToUnion.add(currentRelationKeyToUnion);

      RelationKey currentConfigRelationKey =
          new RelationKey(
              factTableDesc.relationKey.getUserName(),
              factTableDesc.relationKey.getProgramName(),
              factTableDesc.relationKey.getRelationName() + currentSize);
      server.createMaterializedView(
          currentConfigRelationKey.toString(MyriaConstants.STORAGE_SYSTEM_POSTGRESQL),
          PerfEnforceUtils.createUnionQuery(relationKeysToUnion),
          currentWorkerRange);
      server.addDatasetToCatalog(
          currentConfigRelationKey, factTableDesc.schema, currentWorkerRange);
      factTableRelationMapper.put(currentSize, currentConfigRelationKey);
      previousWorkerRange = currentWorkerRange;
      previousRelationKey = currentConfigRelationKey;
    }
    return factTableRelationMapper;
  }

  /*
   * Ingesting dimension tables for broadcasting
   */
  public void ingestDimension(final TableDescriptionEncoding dimTableDesc)
      throws PerfEnforceException {

    Set<Integer> totalWorkers =
        PerfEnforceUtils.getWorkerRangeSet(Collections.max(PerfEnforceDriver.configurations));

    server.parallelIngestDataset(
        dimTableDesc.relationKey,
        dimTableDesc.schema,
        dimTableDesc.delimiter,
        null,
        null,
        null,
        dimTableDesc.source,
        totalWorkers);

    DbQueryScan dbscan = new DbQueryScan(dimTableDesc.relationKey, dimTableDesc.schema);
    /*
     * Is there a better way to broadcast relations?
     */
    final ExchangePairID broadcastID = ExchangePairID.newID();
    int[][] cellPartition = new int[1][];
    int[] allCells = new int[totalWorkers.size()];
    for (int i = 0; i < totalWorkers.size(); i++) {
      allCells[i] = i;
    }
    cellPartition[0] = allCells;

    GenericShuffleProducer producer =
        new GenericShuffleProducer(
            dbscan,
            broadcastID,
            cellPartition,
            MyriaUtils.integerSetToIntArray(totalWorkers),
            new FixValuePartitionFunction(0));

    GenericShuffleConsumer consumer =
        new GenericShuffleConsumer(
            dimTableDesc.schema, broadcastID, MyriaUtils.integerSetToIntArray(totalWorkers));
    DbInsert insert = new DbInsert(consumer, dimTableDesc.relationKey, true);
    Map<Integer, RootOperator[]> workerPlans = new HashMap<>(totalWorkers.size());
    for (Integer workerID : totalWorkers) {
      workerPlans.put(workerID, new RootOperator[] {producer, insert});
    }

    server.submitQueryPlan(new SinkRoot(new EOSSource()), workerPlans).get();
  }

  /*
   * Run Statistics on the table by extending statistics space for each column and running analyze on the table for all
   * workers
   */
  public void setStatisticsAnalyze(final TableDescriptionEncoding t) {
    /*
     * If this table is Fact, we need to make sure we run "analyze" on all versions of the table
     */
    if (t.type.equals("fact")) {
      for (Entry<Integer, RelationKey> entry : factTableRelationMapper.entrySet()) {
        TableDescriptionEncoding temp =
            new TableDescriptionEncoding(
                t.relationKey,
                t.type,
                t.source,
                t.schema,
                t.delimiter,
                t.keys,
                t.corresponding_fact_key);
        temp.relationKey =
            new RelationKey(
                entry.getValue().getUserName(),
                entry.getValue().getProgramName(),
                entry.getValue().getRelationName());
        postgresStatsAnalyzeTable(temp);
      }
    } else {
      postgresStatsAnalyzeTable(t);
    }
  }

  public void postgresStatsAnalyzeTable(final TableDescriptionEncoding t) {
    for (int i = 0; i < t.schema.getColumnNames().size(); i++) {
      server.executeSQLCommand(
          String.format(
              "ALTER TABLE %s ALTER COLUMN %s SET STATISTICS 500;",
              t.relationKey.toString(MyriaConstants.STORAGE_SYSTEM_POSTGRESQL),
              t.schema.getColumnName(i)),
          new HashSet<Integer>(Arrays.asList(1)));
    }
    server.executeSQLCommand(
        String.format(
            "ANALYZE %s;", t.relationKey.toString(MyriaConstants.STORAGE_SYSTEM_POSTGRESQL)),
        new HashSet<Integer>(Arrays.asList(1)));
  }

  public void collectSelectivities(final TableDescriptionEncoding t) {
    List<StatsTableEncoding> statsList = new ArrayList<StatsTableEncoding>();
    if (t.type.equals("fact")) {
      for (Integer currentConfig : PerfEnforceDriver.configurations) {
        RelationKey factRelationKey = factTableRelationMapper.get(currentConfig);
        long factTableTupleCount = server.getDatasetStatus(factRelationKey).getNumTuples();
        statsList.add(
            runTableRanking(
                factRelationKey, factTableTupleCount, currentConfig, t.type, t.keys, t.schema));
      }
    } else {
      RelationKey dimensionTableKey = t.relationKey;
      long dimensionTableTupleCount = server.getDatasetStatus(dimensionTableKey).getNumTuples();
      statsList.add(
          runTableRanking(
              dimensionTableKey,
              dimensionTableTupleCount,
              Collections.max(PerfEnforceDriver.configurations),
              t.type,
              t.keys,
              t.schema));
    }
    PrintWriter statsObjectWriter =
        new PrintWriter(
            PerfEnforceDriver.configurationPath.resolve("stats.json").toString(), "UTF-8");
    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(statsObjectWriter, statsList);
    statsObjectWriter.close();
  }

  /*
   * For each primary key, determine the rank based on the selectivity and return the result
   */
  public StatsTableEncoding runTableRanking(
      final RelationKey relationKey,
      final long tableSize,
      final int config,
      final String type,
      final Set<Integer> keys,
      final Schema schema) {

    List<String> selectivityKeys = new ArrayList<String>();
    List<Double> selectivityList = Arrays.asList(new Double[] {.001, .01, .1});

    String attributeKeyString = PerfEnforceUtils.getAttributeKeyString(keys, schema);
    Schema attributeKeySchema = PerfEnforceUtils.getAttributeKeySchema(keys, schema);

    String tableName = relationKey.toString(MyriaConstants.STORAGE_SYSTEM_POSTGRESQL);

    for (int i = 0; i < selectivityList.size(); i++) {
      String rankingQuery =
          String.format(
              "select %s from (select %s, CAST(rank() over (order by %s asc) AS float)/%s as rank from %s) as r where r.rank >= %s LIMIT 1;",
              attributeKeyString,
              attributeKeyString,
              attributeKeyString,
              tableSize / config,
              tableName,
              selectivityList.get(i));
      String sqlResult =
          server.executeSQLCommandSingleRowSingleWorker(rankingQuery, attributeKeySchema, 1);
      selectivityKeys.add(sqlResult);
    }

    // HACK: We can't properly "count" tuples for tables that are broadcast
    long modifiedSize = tableSize;
    if (type.equalsIgnoreCase("dimension")) {
      modifiedSize = tableSize / Collections.max(PerfEnforceDriver.configurations);
    }

    return new StatsTableEncoding(tableName, modifiedSize, selectivityKeys);
  }

  public void collectFeatures() {

    for (Integer config : PerfEnforceDriver.configurations) {
      Path workerPath =
          Paths.get(PerfEnforceDriver.configurationPath.toString(), config + "_Workers");
      String currentLine = "";

      PrintWriter featureWriter =
          new PrintWriter(workerPath.resolve("TESTING.arff").toString(), "UTF-8");

      featureWriter.write("@relation testing \n");
      featureWriter.write("@attribute numberTables numeric \n");
      featureWriter.write("@attribute postgesEstCostMin numeric \n");
      featureWriter.write("@attribute postgesEstCostMax numeric \n");
      featureWriter.write("@attribute postgesEstNumRows numeric \n");
      featureWriter.write("@attribute postgesEstWidth numeric \n");
      featureWriter.write("@attribute numberOfWorkers numeric \n");
      featureWriter.write("@attribute realTime numeric \n");
      featureWriter.write("\n");
      featureWriter.write("@data \n");

      BufferedReader br =
          new BufferedReader(
              new FileReader(workerPath.resolve("SQLQueries-Generated.txt").toString()));
      while ((currentLine = br.readLine()) != null) {
        currentLine =
            currentLine.replace(
                factTableDescription.relationKey.getRelationName(),
                factTableRelationMapper.get(config).getRelationName());
        String features = getPostgresFeatures(currentLine, 1);
        features = features.substring(features.indexOf("cost"));
        features = features.replace("\"", " ");
        String[] cmd = {
          "sh",
          "-c",
          "echo \""
              + features
              + "\" | sed -e 's/.*cost=//' -e 's/\\.\\./,/' -e 's/ rows=/,/' -e 's/ width=/,/' -e 's/)//'"
        };
        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process p = pb.start();

        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        features = input.readLine();

        // add the extra features -- hacky
        if (currentLine.contains("WHERE")) {
          String[] tables =
              currentLine
                  .substring(currentLine.indexOf("FROM"), currentLine.indexOf("WHERE"))
                  .split(",");
          features = tables.length + "," + features;
        } else {
          features = "1," + features;
        }
        features += "," + config + ",0";
        featureWriter.write(features + "\n");
      }
      featureWriter.close();
      br.close();
    }
  }

  public String getPostgresFeatures(final String query, final int worker) {
    String explainQuery = "EXPLAIN " + query;
    String result =
        server.executeSQLCommandSingleRowSingleWorker(
            explainQuery, Schema.ofFields("explain", Type.STRING_TYPE), worker);

    return result;
  }
}
