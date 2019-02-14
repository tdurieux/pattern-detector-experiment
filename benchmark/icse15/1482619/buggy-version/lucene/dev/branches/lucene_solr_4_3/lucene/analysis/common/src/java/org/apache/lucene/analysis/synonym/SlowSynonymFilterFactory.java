  Merged /lucene/dev/trunk/lucene/analysis/common/src/test/org/apache/lucene/analysis/util/BaseTokenStreamFactoryTestCase.java:r1343946,1343968,1344038,1344041,1344306,1344318,1344441,1344531,1344775,1344826,1344844,1344925,1344938,1344946,1344958,1344967,1345376,1345523,1345540-1345542,1345548,1345869,1346001,1346101,1346322,1346407,1346419,1346434,1346465,1346494,1346500,1346520,1346582,1346645,1346784,1346873-1346975,1346983,1346986,1346995,1346999,1347313,1347323,1347817,1348171,1348236,1348278,1348289,1348348,1348354,1348517,1348524,1348606,1348621,1348630,1348672,1348685,1348723,1348924,1348964,1348974,1348977,1348980,1349012,1349027,1349046,1349058,1349063,1349214,1349361,1349378,1349437,1349502,1349510,1349601,1349612,1349622,1349758,1349795,1349802,1349828,1349897,1349979,1350047,1350050,1350074,1350429,1350436,1350444,1350718,1350723,1350764,1350890,1351108,1351129,1351225,1351460,1351590,1351661,1351672,1351829,1351839,1351879,1352038,1352173,1352238,1352420,1352535,1352600,1352609,1352795,1352942,1352949,1352967,1353004,1353097,1353101,1353433,1353843,1354308,1354455,1354826,1354850,1354864-1354865,1354887,1355001,1355069,1355120,1355238,1355311,1356081,1356083,1356192,1356435-1356437,1356561,1356778,1356797,1357027,1357324,1357331,1357359,1357451,1357595,1357611,1357703,1357707,1357952,1358123,1358149,1358377,1358380,1358386,1358650,1358748,1358751,1359202,1359238,1359249,1359255,1359259,1359275,1359322,1359327,1359355,1359590,1359611,1359827,1359873,1359946,1359953,1360021,1360028,1360205,1360240,1360254,1360272,1360354,1360395,1360451,1360454,1360475,1360619,1360645,1360687,1360931,1360977,1360979,1361091,1361243,1361301,1361408,1361480,1361512,1361517,1361701,1361741,1361745,1361857,1361896,1361910,1361966,1362025,1362074,1362236,1362442,1362667,1363004,1363049,1363082,1363084,1363115,1363161,1363379,1363555,1363803,1363819,1363821,1363871,1363971,1364006,1364031,1364409,1364728,1364940,1364967,1365014,1365166,1365363,1365383,1365390,1365450,1365586,1365602,1365610,1365857,1365865,1365992,1366115,1366241,1366249,1366335,1366360,1366392,1366460,1366509,1366568,1366574,1366588,1366631,1366646,1366716,1366748,1366756,1366775,1367096,1367125,1367178,1367186,1367192,1367194,1367316,1367358,1367362,1367377,1367384,1367386,1367623,1367800,1367916,1368188,1368190,1368286,1369433,1369502,1369874,1369883,1369984,1370297,1370314,1370870,1370904,1370908,1371379,1371478,1371491,1372069,1372071,1372218,1372631,1372687,1373117,1373553,1373598,1373606,1373904,1374115,1374381,1374480,1374490,1374501,1374912,1375486,1375674,1375723,1375725,1376158,1376797,1377702,1377867,1377898,1377972,1378250,1378387,1378392,1379014,1379036,1379195,1379225,1379237,1379275,1380287,1380967,1381490,1381494,1381518,1381523,1381602,1381655,1381685,1381711,1382187,1382209,1382419,1382621,1383011,1383520,1383648,1383654,1383685,1383708,1383735,1384034,1384219-1384220,1384225,1384252-1384253,1384293,1384358,1384394,1384420,1384492,1384522,1384529,1384567,1384597,1384657,1384872,1384895,1384958,1386613-1386614,1386662,1386773,1386790,1386858,1386924,1387259,1387354,1387747,1387778,1387824,1388035,1388260,1388288,1388292,1388830,1389133,1389188,1389507,1391083,1391826,1391829,1391841,1391849,1393171,1393794,1394836,1394983,1395518,1395847,1396317,1397282,1397321,1397452,1397454,1397589,1397628,1397665,1397698,1398086,1398564,1398570,1399474,1400081,1400504,1400565,1401024,1401338,1401340,1401343,1401449,1401461,1401692,1401778,1401798,1401916,1401947,1401963,1402254,1402360,1402367-1402368,1402373,1402389,1402393,1402461,1402463,1402613,1402677,1402742,1402798,1402808,1402813,1402818,1402822,1402831,1402952,1403033,1403056,1403109,1403131,1403353,1403396,1403480,1403490,1403555,1403638,1403641,1403667,1403710,1403779,1403782,1403798-1403799,1403803,1403936,1404042,1404047,1404171,1404318,1404523,1405893,1405912,1405919,1406075,1406204,1406231,1406245,1406252,1406437,1406444,1406454,1406601,1406664,1406698,1406758,1407093,1407107,1407547,1407815,1407817,1408088,1408091,1408125,1408134,1408305,1408313,1408336,1408364,1408368,1408377,1408388,1408398,1408510,1408560,1408646-1408649,1408831,1408873,1408880,1408885,1409199,1409517,1410115,1410593,1410908,1410917,1411060,1411276,1411304,1411334,1411357,1411366,1411370,1411450,1411466,1411471,1411486,1411495,1411523,1411527,1411534,1411536,1411538,1411541,1411544,1411707,1411757,1411768,1411789,1411801,1411812,1411820,1411823,1411887,1411932,1411956,1411996,1412140,1412182,1412803,1413036,1413042,1413052,1413079,1413975,1414176,1414238,1414428,1414744,1414773,1414841,1414853,1414885,1414929,1415060,1415063,1415079,1415136,1415166,1415817,1415873,1416025,1416054,1416058,1416216,1416617,1416744,1417045,1417099,1417622,1417702,1417711,1417736,1417907,1417931,1418030,1418043,1418093,1418100,1418116-1418117,1418337,1418427-1418428,1418507,1418695,1418712,1418725,1418737,1418755-1418756,1418762,1418789-1418791,1418814,1418818,1419019,1419191,1419258,1419466,1419551,1419644,1419665,1419689,1419720,1419866,1419878,1419939,1419952,1419959,1419980,1419991,1420195,1420231,1420248,1420284,1420297,1420327,1420336,1420338,1420362,1420497,1420500,1420507,1420911,1420941,1420951,1420954,1420963,1421034,1421049,1421060-1421061,1421068,1421071,1421078,1421085,1421088-1421089,1421108,1421164,1421326,1421331,1421411,1421446,1421451,1421499,1421505,1421543,1421613,1421644,1421914,1421999,1422368,1422371-1422372,1422384,1422391,1422399,1422466,1422468,1422585,1422608,1422728,1422746,1422775,1422786,1422797,1423056,1423121,1423179,1423182,1423244,1423275,1423591,1423729,1423738,1423748,1423753,1423756,1424263,1424272,1424516,1424755,1424783,1424793,1424796,1424868,1425207,1425306,1425342,1425554,1425561,1425574,1425815,1425818,1426002,1426329,1426373,1426569,1426716,1426746,1426791,1426795,1426826,1426839-1426840,1426892,1426898,1427037,1427213,1427218,1427240,1427258,1427591,1427593,1427598,1427606,1427616,1427618,1427620,1427657,1427872,1428110,1428138,1428156,1428229,1428411,1428671,1428677,1428695,1428823,1428963,1429027,1429075,1429174,1429181,1429188,1430111,1430116,1430290,1430399,1430477,1430725,1430931,1430939,1432256,1432459,1432466,1432472,1432474,1432522,1432646,1432670,1432747,1432774,1432794,1432936,1432993,1433026,1433082,1433109,1433660,1433778,1433817,1433824,1434020,1434022,1434109,1434664,1434984,1435097,1435146,1435191,1436346,1436837,1436859,1437007,1437604,1438036,1438043,1438242,1438283,1438550,1438612,1438655,1438822,1438891,1438977-1438978,1439536,1440069,1440263,1440508,1440518,1441154,1441483,1441520,1441522,1441913,1441941,1441943,1441946,1441970,1441974,1442106,1442112,1442648,1442771,1442815,1442876,1443022,1443050,1443094,1443326,1443672,1443717,1444152,1444239,1444249,1444316,1444468,1444472,1445945,1445971,1446449,1446716,1446729,1446833,1446914,1446926-1446929,1446935,1446981,1447071,1447075,1447084,1447089,1447098,1447308,1447884-1447885,1447952,1448204,1448400,1448422,1448426,1448773,1448852,1448863,1448879,1448932,1449053,1449115,1449141,1449179,1449211,1449240,1449253,1449258,1449323,1449483,1449496,1449576,1449578,1449809,1450012,1450015,1450043,1450275,1450304,1450342,1450369,1450577-1450578,1450594,1450600,1450795,1450800,1451214,1451228,1451322,1451370,1451454,1451584,1451653,1451656,1451659-1451660,1451691,1451765,1451797,1451818,1451838,1451841,1451906,1451931,1451987,1451997-1451998,1452069,1452073,1452090,1452113,1452115,1452143,1452321,1452334,1452483,1452508,1452612,1452626,1453148,1453161,1453461,1453507,1453560,1453965,1454313,1454384,1454761,1454868,1454913,1454918,1454921,1454926,1454932,1454993,1455196,1455269,1455321,1455325,1455368,1455606,1455670,1455753,1455860,1455904,1455911,1455943,1456433,1456435,1456468,1456596,1456683,1456723,1456727,1456731,1456733,1456768,1456938,1456979,1457032,1457132,1457134,1457146,1457154,1457211,1457281,1457292-1457293,1457310,1457380,1457455,1457475,1457494,1457556-1457557,1457572,1457584-1457586,1457591,1457638,1457640,1457646,1457648,1457784,1457816,1457864,1458602,1458848,1458857,1458880,1458887,1459051,1459424,1459537,1459565,1459570,1459596,1459611,1459616,1459618,1459624,1459638,1459646,1459846,1459903,1460039,1460071,1460076-1460077,1460141,1460510,1460580,1460761,1460803,1460980,1461587,1461717,1462145,1462227,1462260,1462306,1462495,1463068,1463191,1463232,1463242,1463245,1463316,1463335,1463543,1464020,1464802,1465032,1465256,1465531,1465575,1465749,1465836,1466078,1466149,1466152,1466291,1466649,1466720,1466962,1467723,1468208,1468256,1468284,1468705,1468841,1469073,1469397,1469508,1469529,1469600,1470496,1470523,1476310-1476328,1480383,1480988,1480998,1481038,1482474
  Merged /lucene/dev/branches/lucene4547/lucene/analysis/common/src/test/org/apache/lucene/analysis/util/BaseTokenStreamFactoryTestCase.java:r1407149-1443597
  Merged /lucene/dev/branches/lucene3846/lucene/analysis/common/src/test/org/apache/lucene/analysis/util/BaseTokenStreamFactoryTestCase.java:r1397170-1403761
  Merged /lucene/dev/branches/lucene4199/lucene/analysis/common/src/test/org/apache/lucene/analysis/util/BaseTokenStreamFactoryTestCase.java:r1358548-1359191
  Merged /lucene/dev/branches/lucene2510/lucene/analysis/common/src/test/org/apache/lucene/analysis/util/BaseTokenStreamFactoryTestCase.java:r1364862-1365496
  Merged /lucene/dev/branches/lucene3969/lucene/analysis/common/src/test/org/apache/lucene/analysis/util/BaseTokenStreamFactoryTestCase.java:r1311219-1324948
  Merged /lucene/dev/branches/branch_3x/lucene/analysis/common/src/test/org/apache/lucene/analysis/util/BaseTokenStreamFactoryTestCase.java:r1232954,1302749,1302808,1303007,1303023,1303269,1303733,1303854,1304295,1304360,1304660,1304904,1305074,1305142,1305681,1305693,1305719,1305741,1305816,1305837,1306929,1307050
  Merged /lucene/dev/branches/branch_4x/lucene/analysis/common/src/test/org/apache/lucene/analysis/util/BaseTokenStreamFactoryTestCase.java:r1469034,1469402,1479892,1480392,1481004,1482527
  Merged /lucene/dev/branches/lucene4055/lucene/analysis/common/src/test/org/apache/lucene/analysis/util/BaseTokenStreamFactoryTestCase.java:r1338960-1343359
  Merged /lucene/dev/branches/pforcodec_3892/lucene/analysis/common/src/test/org/apache/lucene/analysis/util/BaseTokenStreamFactoryTestCase.java:r1352188-1375470
package org.apache.lucene.analysis.synonym;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.*;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory for {@link SlowSynonymFilter} (only used with luceneMatchVersion < 3.4)
 * <pre class="prettyprint" >
 * &lt;fieldType name="text_synonym" class="solr.TextField" positionIncrementGap="100"&gt;
 *   &lt;analyzer&gt;
 *     &lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;
 *     &lt;filter class="solr.SynonymFilterFactory" synonyms="synonyms.txt" ignoreCase="false"
 *             expand="true" tokenizerFactory="solr.WhitespaceTokenizerFactory"/&gt;
 *   &lt;/analyzer&gt;
 * &lt;/fieldType&gt;</pre>
 * @deprecated (3.4) use {@link SynonymFilterFactory} instead. only for precise index backwards compatibility. this factory will be removed in Lucene 5.0
 */
@Deprecated
final class SlowSynonymFilterFactory extends TokenFilterFactory implements ResourceLoaderAware {
  private final String synonyms;
  private final boolean ignoreCase;
  private final boolean expand;
  private final String tf;
  
  public SlowSynonymFilterFactory(Map<String,String> args) {
    super(args);
    synonyms = require(args, "synonyms");
    ignoreCase = getBoolean(args, "ignoreCase", false);
    expand = getBoolean(args, "expand", true);

    tf = get(args, "tokenizerFactory");
    if (!args.isEmpty()) {
      throw new IllegalArgumentException("Unknown parameters: " + args);
    }
  }
  
  public void inform(ResourceLoader loader) throws IOException {
    TokenizerFactory tokFactory = null;
    if( tf != null ){
      tokFactory = loadTokenizerFactory(loader, tf);
    }

    Iterable<String> wlist=loadRules( synonyms, loader );
    
    synMap = new SlowSynonymMap(ignoreCase);
    parseRules(wlist, synMap, "=>", ",", expand,tokFactory);
  }
  
  /**
   * @return a list of all rules
   */
  protected Iterable<String> loadRules( String synonyms, ResourceLoader loader ) throws IOException {
    List<String> wlist=null;
    File synonymFile = new File(synonyms);
    if (synonymFile.exists()) {
      wlist = getLines(loader, synonyms);
    } else  {
      List<String> files = splitFileNames(synonyms);
      wlist = new ArrayList<String>();
      for (String file : files) {
        List<String> lines = getLines(loader, file.trim());
        wlist.addAll(lines);
      }
    }
    return wlist;
  }

  private SlowSynonymMap synMap;

  static void parseRules(Iterable<String> rules, SlowSynonymMap map, String mappingSep,
    String synSep, boolean expansion, TokenizerFactory tokFactory) throws IOException {
    int count=0;
    for (String rule : rules) {
      // To use regexes, we need an expression that specifies an odd number of chars.
      // This can't really be done with string.split(), and since we need to
      // do unescaping at some point anyway, we wouldn't be saving any effort
      // by using regexes.

      List<String> mapping = splitSmart(rule, mappingSep, false);

      List<List<String>> source;
      List<List<String>> target;

      if (mapping.size() > 2) {
        throw new IllegalArgumentException("Invalid Synonym Rule:" + rule);
      } else if (mapping.size()==2) {
        source = getSynList(mapping.get(0), synSep, tokFactory);
        target = getSynList(mapping.get(1), synSep, tokFactory);
      } else {
        source = getSynList(mapping.get(0), synSep, tokFactory);
        if (expansion) {
          // expand to all arguments
          target = source;
        } else {
          // reduce to first argument
          target = new ArrayList<List<String>>(1);
          target.add(source.get(0));
        }
      }

      boolean includeOrig=false;
      for (List<String> fromToks : source) {
        count++;
        for (List<String> toToks : target) {
          map.add(fromToks,
                  SlowSynonymMap.makeTokens(toToks),
                  includeOrig,
                  true
          );
        }
      }
    }
  }

  // a , b c , d e f => [[a],[b,c],[d,e,f]]
  private static List<List<String>> getSynList(String str, String separator, TokenizerFactory tokFactory) throws IOException {
    List<String> strList = splitSmart(str, separator, false);
    // now split on whitespace to get a list of token strings
    List<List<String>> synList = new ArrayList<List<String>>();
    for (String toks : strList) {
      List<String> tokList = tokFactory == null ?
        splitWS(toks, true) : splitByTokenizer(toks, tokFactory);
      synList.add(tokList);
    }
    return synList;
  }

  private static List<String> splitByTokenizer(String source, TokenizerFactory tokFactory) throws IOException{
    StringReader reader = new StringReader( source );
    TokenStream ts = loadTokenizer(tokFactory, reader);
    List<String> tokList = new ArrayList<String>();
    try {
      CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);
      while (ts.incrementToken()){
        if( termAtt.length() > 0 )
          tokList.add( termAtt.toString() );
      }
    } finally{
      reader.close();
    }
    return tokList;
  }

  private TokenizerFactory loadTokenizerFactory(ResourceLoader loader, String cname) throws IOException {
    Map<String,String> args = new HashMap<String,String>();
    args.put("luceneMatchVersion", getLuceneMatchVersion().toString());
    Class<? extends TokenizerFactory> clazz = loader.findClass(cname, TokenizerFactory.class);
    try {
      TokenizerFactory tokFactory = clazz.getConstructor(Map.class).newInstance(args);
      if (tokFactory instanceof ResourceLoaderAware) {
        ((ResourceLoaderAware) tokFactory).inform(loader);
      }
      return tokFactory;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static TokenStream loadTokenizer(TokenizerFactory tokFactory, Reader reader){
    return tokFactory.create( reader );
  }

  public SlowSynonymMap getSynonymMap() {
    return synMap;
  }

  public SlowSynonymFilter create(TokenStream input) {
    return new SlowSynonymFilter(input,synMap);
  }
  
  public static List<String> splitWS(String s, boolean decode) {
    ArrayList<String> lst = new ArrayList<String>(2);
    StringBuilder sb = new StringBuilder();
    int pos=0, end=s.length();
    while (pos < end) {
      char ch = s.charAt(pos++);
      if (Character.isWhitespace(ch)) {
        if (sb.length() > 0) {
          lst.add(sb.toString());
          sb=new StringBuilder();
        }
        continue;
      }

      if (ch=='\\') {
        if (!decode) sb.append(ch);
        if (pos>=end) break;  // ERROR, or let it go?
        ch = s.charAt(pos++);
        if (decode) {
          switch(ch) {
            case 'n' : ch='\n'; break;
            case 't' : ch='\t'; break;
            case 'r' : ch='\r'; break;
            case 'b' : ch='\b'; break;
            case 'f' : ch='\f'; break;
          }
        }
      }

      sb.append(ch);
    }

    if (sb.length() > 0) {
      lst.add(sb.toString());
    }

    return lst;
  }
  
  /** Splits a backslash escaped string on the separator.
   * <p>
   * Current backslash escaping supported:
   * <br> \n \t \r \b \f are escaped the same as a Java String
   * <br> Other characters following a backslash are produced verbatim (\c => c)
   *
   * @param s  the string to split
   * @param separator the separator to split on
   * @param decode decode backslash escaping
   */
  public static List<String> splitSmart(String s, String separator, boolean decode) {
    ArrayList<String> lst = new ArrayList<String>(2);
    StringBuilder sb = new StringBuilder();
    int pos=0, end=s.length();
    while (pos < end) {
      if (s.startsWith(separator,pos)) {
        if (sb.length() > 0) {
          lst.add(sb.toString());
          sb=new StringBuilder();
        }
        pos+=separator.length();
        continue;
      }

      char ch = s.charAt(pos++);
      if (ch=='\\') {
        if (!decode) sb.append(ch);
        if (pos>=end) break;  // ERROR, or let it go?
        ch = s.charAt(pos++);
        if (decode) {
          switch(ch) {
            case 'n' : ch='\n'; break;
            case 't' : ch='\t'; break;
            case 'r' : ch='\r'; break;
            case 'b' : ch='\b'; break;
            case 'f' : ch='\f'; break;
          }
        }
      }

      sb.append(ch);
    }

    if (sb.length() > 0) {
      lst.add(sb.toString());
    }

    return lst;
  }
}
