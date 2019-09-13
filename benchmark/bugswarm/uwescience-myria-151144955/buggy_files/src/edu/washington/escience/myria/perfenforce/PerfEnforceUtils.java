/**
 *
 */
package edu.washington.escience.myria.perfenforce;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;

import edu.washington.escience.myria.RelationKey;
import edu.washington.escience.myria.Schema;
import edu.washington.escience.myria.Type;
import edu.washington.escience.myria.perfenforce.encoding.TableDescriptionEncoding;

/**
 * Helper Methods
 */
public class PerfEnforceUtils {

  public static List<TableDescriptionEncoding> getTablesOfType(
      final String type, final String configFilePath) throws PerfEnforceException {
    List<TableDescriptionEncoding> listTablesOfType = new ArrayList<TableDescriptionEncoding>();
    Gson gson = new Gson();
    String stringFromFile;
    try {
      stringFromFile = Files.toString(new File(configFilePath), Charsets.UTF_8);
      TableDescriptionEncoding[] tableList =
          gson.fromJson(stringFromFile, TableDescriptionEncoding[].class);

      for (TableDescriptionEncoding currentTable : tableList) {
        if (currentTable.type.equals(type) || type.equals("*")) {
          listTablesOfType.add(currentTable);
        }
      }
    } catch (IOException e) {
      throw new PerfEnforceException(e);
    }
    return listTablesOfType;
  }

  public static Set<Integer> getWorkerRangeSet(final int limit) {
    Set<Integer> seq = new HashSet<Integer>();
    for (int i = 1; i <= limit; i++) {
      seq.add(i);
    }
    return seq;
  }

  public static int[] getRangeInclusiveArray(final int min, final int max) {
    int numberElements = (max - min) + 1;
    int[] intArray = new int[numberElements];
    for (int i = 0; i < numberElements; i++) {
      intArray[i] = min + i;
    }
    return intArray;
  }

  /*
   * Construct UNION view
   */
  public static String createUnionQuery(final List<RelationKey> keysToUnion) {
    String sql = "";
    for (RelationKey table : keysToUnion) {
      sql +=
          String.format(
              "select * from \"%s:%s:%s\"",
              table.getUserName(),
              table.getProgramName(),
              table.getRelationName());
      if (table != keysToUnion.get(keysToUnion.size() - 1)) {
        sql += " UNION ALL ";
      }
    }
    return sql;
  }

  public static String getAttributeKeyString(final Set<Integer> keys, final Schema schema) {
    String keyString = "";
    int counter = 1;
    for (int key : keys) {
      keyString += schema.getColumnName(key);
      if (counter != keys.size()) {
        keyString += ",";
      }
      counter++;
    }
    return keyString;
  }

  public static Schema getAttributeKeySchema(final Set<Integer> keys, final Schema schema) {
    Schema keySchema = Schema.EMPTY_SCHEMA;
    for (int key : keys) {
      keySchema =
          Schema.appendColumn(keySchema, schema.getColumnType(key), schema.getColumnName(key));
    }
    return keySchema;
  }

  public static String getMaxFeature(final String sqlQuery, final int configuration) {
    String explainQuery = "EXPLAIN " + sqlQuery;
    explainQuery = explainQuery.replace("lineitem", "lineitem" + configuration);

    List<String> featuresList = new ArrayList<String>();
    String maxFeature = "";
    double maxCost = Integer.MIN_VALUE;

    for (int w = 1; w <= configuration; w++) {
      String features = "";
      features =
          server.executeSQLCommandSingleRowSingleWorker(
              explainQuery, Schema.ofFields("explain", Type.STRING_TYPE), w);

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
      Process p;

      p = pb.start();
      BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
      features = input.readLine();

      if (sqlQuery.contains("WHERE")) {
        String[] tables =
            sqlQuery.substring(sqlQuery.indexOf("FROM"), sqlQuery.indexOf("WHERE")).split(",");
        features = tables.length + "," + features;
      } else {
        features = "1," + features;
      }

      features += "," + configuration + ",0";
      featuresList.add(features);
    }

    for (String f : featuresList) {
      double cost = Double.parseDouble(f.split(",")[2]);
      if (cost > maxCost) {
        maxCost = cost;
        maxFeature = f;
      }
    }

    return maxFeature;
  }
}
