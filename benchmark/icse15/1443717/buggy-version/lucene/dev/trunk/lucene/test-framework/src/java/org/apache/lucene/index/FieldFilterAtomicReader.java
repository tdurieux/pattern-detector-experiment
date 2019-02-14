  Merged /lucene/dev/branches/lucene3846/lucene/codecs:r1397170-1403761
  Merged /lucene/dev/branches/lucene4199/lucene/codecs:r1358548-1359191
  Merged /lucene/dev/branches/slowclosing/lucene/codecs:r1393532-1393785
  Merged /lucene/dev/branches/lucene3969/lucene/codecs:r1311219-1324948
  Merged /lucene/dev/branches/branch_3x/lucene/codecs:r1232954,1302749,1302808,1303007,1303023,1303269,1303733,1303854,1304295,1304360,1304660,1304904,1305074,1305142,1305681,1305693,1305719,1305741,1305816,1305837,1306929,1307050
  Merged /lucene/dev/branches/branch_4x/lucene/codecs:r1344391,1344929,1348012,1348274,1348293,1348919,1348951,1349048,1349340,1349446,1349991,1353701,1355203,1356608,1359358,1363876,1364063,1364069,1367391,1367489,1367833,1368975,1369226,1371960,1374622,1375497,1375558,1376547,1378442,1378591,1379175,1380802,1381204,1383216,1386921,1388425,1389811,1389929,1392460,1393832,1394309,1395515,1404227,1405891
  Merged /lucene/dev/branches/lucene_solr_4_0/lucene/codecs:r1388937,1389448,1390046,1394306
  Merged /lucene/dev/branches/cleanup2878/lucene/codecs:r1403701-1403781
  Merged /lucene/dev/branches/lucene2510/lucene/codecs:r1364862-1365496
  Merged /lucene/dev/branches/lucene3312/lucene/codecs:r1357905-1379945
  Merged /lucene/dev/branches/lucene4055/lucene/codecs:r1338960-1343359
  Merged /lucene/dev/branches/pforcodec_3892/lucene/codecs:r1352188-1375470
  Merged /lucene/dev/branches/ghost_of_4456/lucene/codecs:r1394211-1394305
  Merged /lucene/dev/branches/lucene4446/lucene/codecs:r1397400-1398082
  Merged /lucene/dev/branches/lucene4547/lucene/codecs:r1407149-1443597
  Merged /lucene/dev/branches/solr3733/lucene/codecs:r1388080-1388269
  Merged /lucene/dev/branches/lucene4547/lucene/ivy-settings.xml:r1407149-1443597
  Merged /lucene/dev/branches/lucene4547/lucene/MIGRATE.txt:r1407149-1443597
package org.apache.lucene.index;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.apache.lucene.util.FilterIterator;

/**
 * A {@link FilterAtomicReader} that exposes only a subset
 * of fields from the underlying wrapped reader.
 */
public final class FieldFilterAtomicReader extends FilterAtomicReader {
  
  private final Set<String> fields;
  private final boolean negate;
  private final FieldInfos fieldInfos;

  public FieldFilterAtomicReader(AtomicReader in, Set<String> fields, boolean negate) {
    super(in);
    this.fields = fields;
    this.negate = negate;
    ArrayList<FieldInfo> filteredInfos = new ArrayList<FieldInfo>();
    for (FieldInfo fi : in.getFieldInfos()) {
      if (hasField(fi.name)) {
        filteredInfos.add(fi);
      }
    }
    fieldInfos = new FieldInfos(filteredInfos.toArray(new FieldInfo[filteredInfos.size()]));
  }
  
  boolean hasField(String field) {
    return negate ^ fields.contains(field);
  }

  @Override
  public FieldInfos getFieldInfos() {
    return fieldInfos;
  }

  @Override
  public Fields getTermVectors(int docID) throws IOException {
    Fields f = super.getTermVectors(docID);
    if (f == null) {
      return null;
    }
    f = new FieldFilterFields(f);
    // we need to check for emptyness, so we can return
    // null:
    return f.iterator().hasNext() ? f : null;
  }

  @Override
  public void document(final int docID, final StoredFieldVisitor visitor) throws IOException {
    super.document(docID, new StoredFieldVisitor() {
      @Override
      public void binaryField(FieldInfo fieldInfo, byte[] value) throws IOException {
        visitor.binaryField(fieldInfo, value);
      }

      @Override
      public void stringField(FieldInfo fieldInfo, String value) throws IOException {
        visitor.stringField(fieldInfo, value);
      }

      @Override
      public void intField(FieldInfo fieldInfo, int value) throws IOException {
        visitor.intField(fieldInfo, value);
      }

      @Override
      public void longField(FieldInfo fieldInfo, long value) throws IOException {
        visitor.longField(fieldInfo, value);
      }

      @Override
      public void floatField(FieldInfo fieldInfo, float value) throws IOException {
        visitor.floatField(fieldInfo, value);
      }

      @Override
      public void doubleField(FieldInfo fieldInfo, double value) throws IOException {
        visitor.doubleField(fieldInfo, value);
      }

      @Override
      public Status needsField(FieldInfo fieldInfo) throws IOException {
        return hasField(fieldInfo.name) ? visitor.needsField(fieldInfo) : Status.NO;
      }
    });
  }

  @Override
  public Fields fields() throws IOException {
    final Fields f = super.fields();
    return (f == null) ? null : new FieldFilterFields(f);
  }

  @Override
  public DocValues docValues(String field) throws IOException {
    return hasField(field) ? super.docValues(field) : null;
  }

  @Override
  public DocValues normValues(String field) throws IOException {
    return hasField(field) ? super.normValues(field) : null;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("FieldFilterAtomicReader(reader=");
    sb.append(in).append(", fields=");
    if (negate) sb.append('!');
    return sb.append(fields).append(')').toString();
  }
  
  private class FieldFilterFields extends FilterFields {

    public FieldFilterFields(Fields in) {
      super(in);
    }

    @Override
    public int size() {
      // this information is not cheap, return -1 like MultiFields does:
      return -1;
    }

    @Override
    public Iterator<String> iterator() {
      return new FilterIterator<String, String>(super.iterator()) {
        @Override
        protected boolean predicateFunction(String field) {
          return hasField(field);
        }
      };
    }

    @Override
    public Terms terms(String field) throws IOException {
      return hasField(field) ? super.terms(field) : null;
    }
    
  }
  
}
