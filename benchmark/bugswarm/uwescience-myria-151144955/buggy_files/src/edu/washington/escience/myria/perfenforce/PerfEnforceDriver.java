/**
 *
 */
package edu.washington.escience.myria.perfenforce;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.Context;

import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;

import edu.washington.escience.myria.parallel.Server;
import edu.washington.escience.myria.perfenforce.encoding.TableDescriptionEncoding;

/**
 * The PerfEnforce Driver
 *
 */
public class PerfEnforceDriver {

  @Context private Server server;

  protected static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(PerfEnforceDriver.class);

  final public static ArrayList<Integer> configurations =
      (ArrayList<Integer>) Arrays.asList(4, 6, 8, 10, 12);

  public static Path configurationPath;

  private boolean isDonePSLA;
  private PerfEnforceOnlineLearning perfenforceOnlineLearning;

  @Inject
  public PerfEnforceDriver(final Path configurationPath) {
    this.configurationPath = configurationPath;
    queryCounter = 0;
    isDonePSLA = false;
  }

  /*
   * Fetch necessary files from S3
   */
  public void fetchS3Files() throws IOException {
    AmazonS3 s3Client = new AmazonS3Client(new AnonymousAWSCredentials());
    String currentFile = "";
    BufferedReader bufferedReader =
        new BufferedReader(new FileReader(configurationPath + "filesToFetch.txt"));
    while ((currentFile = bufferedReader.readLine()) != null) {
      Path filePath = configurationPath.resolve(currentFile);
      s3Client.getObject(
          new GetObjectRequest("perfenforce", currentFile), new File(filePath.toString()));
    }
  }

  public void preparePSLA() {
    fetchS3Files();

    PerfEnforceDataPreparation perfenforceDataPrepare = new PerfEnforceDataPreparation();
    List<TableDescriptionEncoding> allTables =
        PerfEnforceUtils.getTablesOfType(
            "*", configurationPath.resolve("SchemaDefinition.json").toString());
    for (TableDescriptionEncoding currentTable : allTables) {
      if (currentTable.type.equalsIgnoreCase("fact")) {
        perfenforceDataPrepare.ingestFact(currentTable);
      } else {
        perfenforceDataPrepare.ingestDimension(currentTable);
      }
      perfenforceDataPrepare.setStatisticsAnalyze(currentTable);
      perfenforceDataPrepare.collectSelectivities(currentTable);
    }

    PSLAManagerWrapper pslaManager = new PSLAManagerWrapper();
    pslaManager.generateQueries();

    perfenforceDataPrepare.collectFeatures();

    pslaManager.generatePSLA();
    isDonePSLA = true;
  }

  /*
   * Start the tier and begin the new query session
   */
  public boolean isDonePSLA() {
    return isDonePSLA;
  }

  public void setTier(final int tier) {
    perfenforceOnlineLearning = new PerfEnforceOnlineLearning(tier);
  }

  public void findSLA(final String querySQL) {
    perfenforceOnlineLearning.findSLA(querySQL);
  }

  public void recordRealRuntime(final Double queryRuntime) {
    perfenforceOnlineLearning.recordRealRuntime(queryRuntime);
  }

  public QueryMetaData getCurrentQuery() {
    return perfenforceOnlineLearning.getCurrentQuery();
  }

  public QueryMetaData getPreviousQuery() {
    return perfenforceOnlineLearning.getPreviousQuery();
  }

  public int getClusterSize() {
    return perfenforceOnlineLearning.getClusterSize();
  }
}
