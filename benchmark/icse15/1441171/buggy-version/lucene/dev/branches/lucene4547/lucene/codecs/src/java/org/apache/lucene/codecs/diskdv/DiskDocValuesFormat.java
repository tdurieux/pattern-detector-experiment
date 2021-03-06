package org.apache.lucene.codecs.diskdv;

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

import org.apache.lucene.codecs.DocValuesConsumer;
import org.apache.lucene.codecs.DocValuesProducer;
import org.apache.lucene.codecs.DocValuesFormat;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.index.SegmentWriteState;

/**
 * DocValues format that keeps everything on disk.
 * <p>
 * Internally there are only 2 field types:
 * <ul>
 *   <li>BINARY: a big byte[].
 *   <li>NUMERIC: packed ints
 * </ul>
 * SORTED is encoded as BINARY + NUMERIC
 * <p>
 * NOTE: Don't use this format in production (its not very efficient).
 * Most likely you would want some parts in RAM, other parts on disk. 
 * <p>
 * @lucene.experimental
 */
public final class DiskDocValuesFormat extends DocValuesFormat {

  public DiskDocValuesFormat() {
    super("Disk");
  }

  @Override
  public DocValuesConsumer fieldsConsumer(SegmentWriteState state) throws IOException {
    return new DiskDocValuesConsumer(state);
  }

  @Override
  public DocValuesProducer fieldsProducer(SegmentReadState state) throws IOException {
    return new DiskDocValuesProducer(state);
  }
  
  static final String DATA_CODEC = "DiskDocValuesData";
  static final String METADATA_CODEC = "DiskDocValuesMetadata";
  static final int VERSION_START = 0;
  static final int VERSION_CURRENT = VERSION_START;
}
