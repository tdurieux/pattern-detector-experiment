// This file has been automatically generated, DO NOT EDIT

package org.apache.lucene.util.packed;

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

/**
 * Efficient sequential read/write of packed integers.
 */
final class BulkOperationPacked41 extends BulkOperation {
    @Override
    public int blockCount() {
      return 41;
    }

    @Override
    public int valueCount() {
      return 64;
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
      assert blocksOffset + iterations * blockCount() <= blocks.length;
      assert valuesOffset + iterations * valueCount() <= values.length;
      for (int i = 0; i < iterations; ++i) {
        final long block0 = blocks[blocksOffset++];
        values[valuesOffset++] = block0 >>> 23;
        final long block1 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block0 & 8388607L) << 18) | (block1 >>> 46);
        values[valuesOffset++] = (block1 >>> 5) & 2199023255551L;
        final long block2 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block1 & 31L) << 36) | (block2 >>> 28);
        final long block3 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block2 & 268435455L) << 13) | (block3 >>> 51);
        values[valuesOffset++] = (block3 >>> 10) & 2199023255551L;
        final long block4 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block3 & 1023L) << 31) | (block4 >>> 33);
        final long block5 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block4 & 8589934591L) << 8) | (block5 >>> 56);
        values[valuesOffset++] = (block5 >>> 15) & 2199023255551L;
        final long block6 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block5 & 32767L) << 26) | (block6 >>> 38);
        final long block7 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block6 & 274877906943L) << 3) | (block7 >>> 61);
        values[valuesOffset++] = (block7 >>> 20) & 2199023255551L;
        final long block8 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block7 & 1048575L) << 21) | (block8 >>> 43);
        values[valuesOffset++] = (block8 >>> 2) & 2199023255551L;
        final long block9 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block8 & 3L) << 39) | (block9 >>> 25);
        final long block10 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block9 & 33554431L) << 16) | (block10 >>> 48);
        values[valuesOffset++] = (block10 >>> 7) & 2199023255551L;
        final long block11 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block10 & 127L) << 34) | (block11 >>> 30);
        final long block12 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block11 & 1073741823L) << 11) | (block12 >>> 53);
        values[valuesOffset++] = (block12 >>> 12) & 2199023255551L;
        final long block13 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block12 & 4095L) << 29) | (block13 >>> 35);
        final long block14 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block13 & 34359738367L) << 6) | (block14 >>> 58);
        values[valuesOffset++] = (block14 >>> 17) & 2199023255551L;
        final long block15 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block14 & 131071L) << 24) | (block15 >>> 40);
        final long block16 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block15 & 1099511627775L) << 1) | (block16 >>> 63);
        values[valuesOffset++] = (block16 >>> 22) & 2199023255551L;
        final long block17 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block16 & 4194303L) << 19) | (block17 >>> 45);
        values[valuesOffset++] = (block17 >>> 4) & 2199023255551L;
        final long block18 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block17 & 15L) << 37) | (block18 >>> 27);
        final long block19 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block18 & 134217727L) << 14) | (block19 >>> 50);
        values[valuesOffset++] = (block19 >>> 9) & 2199023255551L;
        final long block20 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block19 & 511L) << 32) | (block20 >>> 32);
        final long block21 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block20 & 4294967295L) << 9) | (block21 >>> 55);
        values[valuesOffset++] = (block21 >>> 14) & 2199023255551L;
        final long block22 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block21 & 16383L) << 27) | (block22 >>> 37);
        final long block23 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block22 & 137438953471L) << 4) | (block23 >>> 60);
        values[valuesOffset++] = (block23 >>> 19) & 2199023255551L;
        final long block24 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block23 & 524287L) << 22) | (block24 >>> 42);
        values[valuesOffset++] = (block24 >>> 1) & 2199023255551L;
        final long block25 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block24 & 1L) << 40) | (block25 >>> 24);
        final long block26 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block25 & 16777215L) << 17) | (block26 >>> 47);
        values[valuesOffset++] = (block26 >>> 6) & 2199023255551L;
        final long block27 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block26 & 63L) << 35) | (block27 >>> 29);
        final long block28 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block27 & 536870911L) << 12) | (block28 >>> 52);
        values[valuesOffset++] = (block28 >>> 11) & 2199023255551L;
        final long block29 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block28 & 2047L) << 30) | (block29 >>> 34);
        final long block30 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block29 & 17179869183L) << 7) | (block30 >>> 57);
        values[valuesOffset++] = (block30 >>> 16) & 2199023255551L;
        final long block31 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block30 & 65535L) << 25) | (block31 >>> 39);
        final long block32 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block31 & 549755813887L) << 2) | (block32 >>> 62);
        values[valuesOffset++] = (block32 >>> 21) & 2199023255551L;
        final long block33 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block32 & 2097151L) << 20) | (block33 >>> 44);
        values[valuesOffset++] = (block33 >>> 3) & 2199023255551L;
        final long block34 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block33 & 7L) << 38) | (block34 >>> 26);
        final long block35 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block34 & 67108863L) << 15) | (block35 >>> 49);
        values[valuesOffset++] = (block35 >>> 8) & 2199023255551L;
        final long block36 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block35 & 255L) << 33) | (block36 >>> 31);
        final long block37 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block36 & 2147483647L) << 10) | (block37 >>> 54);
        values[valuesOffset++] = (block37 >>> 13) & 2199023255551L;
        final long block38 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block37 & 8191L) << 28) | (block38 >>> 36);
        final long block39 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block38 & 68719476735L) << 5) | (block39 >>> 59);
        values[valuesOffset++] = (block39 >>> 18) & 2199023255551L;
        final long block40 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block39 & 262143L) << 23) | (block40 >>> 41);
        values[valuesOffset++] = block40 & 2199023255551L;
      }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
      assert blocksOffset + 8 * iterations * blockCount() <= blocks.length;
      assert valuesOffset + iterations * valueCount() <= values.length;
      for (int i = 0; i < iterations; ++i) {
        final long byte0 = blocks[blocksOffset++] & 0xFF;
        final long byte1 = blocks[blocksOffset++] & 0xFF;
        final long byte2 = blocks[blocksOffset++] & 0xFF;
        final long byte3 = blocks[blocksOffset++] & 0xFF;
        final long byte4 = blocks[blocksOffset++] & 0xFF;
        final long byte5 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = (byte0 << 33) | (byte1 << 25) | (byte2 << 17) | (byte3 << 9) | (byte4 << 1) | (byte5 >>> 7);
        final long byte6 = blocks[blocksOffset++] & 0xFF;
        final long byte7 = blocks[blocksOffset++] & 0xFF;
        final long byte8 = blocks[blocksOffset++] & 0xFF;
        final long byte9 = blocks[blocksOffset++] & 0xFF;
        final long byte10 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte5 & 127) << 34) | (byte6 << 26) | (byte7 << 18) | (byte8 << 10) | (byte9 << 2) | (byte10 >>> 6);
        final long byte11 = blocks[blocksOffset++] & 0xFF;
        final long byte12 = blocks[blocksOffset++] & 0xFF;
        final long byte13 = blocks[blocksOffset++] & 0xFF;
        final long byte14 = blocks[blocksOffset++] & 0xFF;
        final long byte15 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte10 & 63) << 35) | (byte11 << 27) | (byte12 << 19) | (byte13 << 11) | (byte14 << 3) | (byte15 >>> 5);
        final long byte16 = blocks[blocksOffset++] & 0xFF;
        final long byte17 = blocks[blocksOffset++] & 0xFF;
        final long byte18 = blocks[blocksOffset++] & 0xFF;
        final long byte19 = blocks[blocksOffset++] & 0xFF;
        final long byte20 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte15 & 31) << 36) | (byte16 << 28) | (byte17 << 20) | (byte18 << 12) | (byte19 << 4) | (byte20 >>> 4);
        final long byte21 = blocks[blocksOffset++] & 0xFF;
        final long byte22 = blocks[blocksOffset++] & 0xFF;
        final long byte23 = blocks[blocksOffset++] & 0xFF;
        final long byte24 = blocks[blocksOffset++] & 0xFF;
        final long byte25 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte20 & 15) << 37) | (byte21 << 29) | (byte22 << 21) | (byte23 << 13) | (byte24 << 5) | (byte25 >>> 3);
        final long byte26 = blocks[blocksOffset++] & 0xFF;
        final long byte27 = blocks[blocksOffset++] & 0xFF;
        final long byte28 = blocks[blocksOffset++] & 0xFF;
        final long byte29 = blocks[blocksOffset++] & 0xFF;
        final long byte30 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte25 & 7) << 38) | (byte26 << 30) | (byte27 << 22) | (byte28 << 14) | (byte29 << 6) | (byte30 >>> 2);
        final long byte31 = blocks[blocksOffset++] & 0xFF;
        final long byte32 = blocks[blocksOffset++] & 0xFF;
        final long byte33 = blocks[blocksOffset++] & 0xFF;
        final long byte34 = blocks[blocksOffset++] & 0xFF;
        final long byte35 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte30 & 3) << 39) | (byte31 << 31) | (byte32 << 23) | (byte33 << 15) | (byte34 << 7) | (byte35 >>> 1);
        final long byte36 = blocks[blocksOffset++] & 0xFF;
        final long byte37 = blocks[blocksOffset++] & 0xFF;
        final long byte38 = blocks[blocksOffset++] & 0xFF;
        final long byte39 = blocks[blocksOffset++] & 0xFF;
        final long byte40 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte35 & 1) << 40) | (byte36 << 32) | (byte37 << 24) | (byte38 << 16) | (byte39 << 8) | byte40;
        final long byte41 = blocks[blocksOffset++] & 0xFF;
        final long byte42 = blocks[blocksOffset++] & 0xFF;
        final long byte43 = blocks[blocksOffset++] & 0xFF;
        final long byte44 = blocks[blocksOffset++] & 0xFF;
        final long byte45 = blocks[blocksOffset++] & 0xFF;
        final long byte46 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = (byte41 << 33) | (byte42 << 25) | (byte43 << 17) | (byte44 << 9) | (byte45 << 1) | (byte46 >>> 7);
        final long byte47 = blocks[blocksOffset++] & 0xFF;
        final long byte48 = blocks[blocksOffset++] & 0xFF;
        final long byte49 = blocks[blocksOffset++] & 0xFF;
        final long byte50 = blocks[blocksOffset++] & 0xFF;
        final long byte51 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte46 & 127) << 34) | (byte47 << 26) | (byte48 << 18) | (byte49 << 10) | (byte50 << 2) | (byte51 >>> 6);
        final long byte52 = blocks[blocksOffset++] & 0xFF;
        final long byte53 = blocks[blocksOffset++] & 0xFF;
        final long byte54 = blocks[blocksOffset++] & 0xFF;
        final long byte55 = blocks[blocksOffset++] & 0xFF;
        final long byte56 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte51 & 63) << 35) | (byte52 << 27) | (byte53 << 19) | (byte54 << 11) | (byte55 << 3) | (byte56 >>> 5);
        final long byte57 = blocks[blocksOffset++] & 0xFF;
        final long byte58 = blocks[blocksOffset++] & 0xFF;
        final long byte59 = blocks[blocksOffset++] & 0xFF;
        final long byte60 = blocks[blocksOffset++] & 0xFF;
        final long byte61 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte56 & 31) << 36) | (byte57 << 28) | (byte58 << 20) | (byte59 << 12) | (byte60 << 4) | (byte61 >>> 4);
        final long byte62 = blocks[blocksOffset++] & 0xFF;
        final long byte63 = blocks[blocksOffset++] & 0xFF;
        final long byte64 = blocks[blocksOffset++] & 0xFF;
        final long byte65 = blocks[blocksOffset++] & 0xFF;
        final long byte66 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte61 & 15) << 37) | (byte62 << 29) | (byte63 << 21) | (byte64 << 13) | (byte65 << 5) | (byte66 >>> 3);
        final long byte67 = blocks[blocksOffset++] & 0xFF;
        final long byte68 = blocks[blocksOffset++] & 0xFF;
        final long byte69 = blocks[blocksOffset++] & 0xFF;
        final long byte70 = blocks[blocksOffset++] & 0xFF;
        final long byte71 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte66 & 7) << 38) | (byte67 << 30) | (byte68 << 22) | (byte69 << 14) | (byte70 << 6) | (byte71 >>> 2);
        final long byte72 = blocks[blocksOffset++] & 0xFF;
        final long byte73 = blocks[blocksOffset++] & 0xFF;
        final long byte74 = blocks[blocksOffset++] & 0xFF;
        final long byte75 = blocks[blocksOffset++] & 0xFF;
        final long byte76 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte71 & 3) << 39) | (byte72 << 31) | (byte73 << 23) | (byte74 << 15) | (byte75 << 7) | (byte76 >>> 1);
        final long byte77 = blocks[blocksOffset++] & 0xFF;
        final long byte78 = blocks[blocksOffset++] & 0xFF;
        final long byte79 = blocks[blocksOffset++] & 0xFF;
        final long byte80 = blocks[blocksOffset++] & 0xFF;
        final long byte81 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte76 & 1) << 40) | (byte77 << 32) | (byte78 << 24) | (byte79 << 16) | (byte80 << 8) | byte81;
        final long byte82 = blocks[blocksOffset++] & 0xFF;
        final long byte83 = blocks[blocksOffset++] & 0xFF;
        final long byte84 = blocks[blocksOffset++] & 0xFF;
        final long byte85 = blocks[blocksOffset++] & 0xFF;
        final long byte86 = blocks[blocksOffset++] & 0xFF;
        final long byte87 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = (byte82 << 33) | (byte83 << 25) | (byte84 << 17) | (byte85 << 9) | (byte86 << 1) | (byte87 >>> 7);
        final long byte88 = blocks[blocksOffset++] & 0xFF;
        final long byte89 = blocks[blocksOffset++] & 0xFF;
        final long byte90 = blocks[blocksOffset++] & 0xFF;
        final long byte91 = blocks[blocksOffset++] & 0xFF;
        final long byte92 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte87 & 127) << 34) | (byte88 << 26) | (byte89 << 18) | (byte90 << 10) | (byte91 << 2) | (byte92 >>> 6);
        final long byte93 = blocks[blocksOffset++] & 0xFF;
        final long byte94 = blocks[blocksOffset++] & 0xFF;
        final long byte95 = blocks[blocksOffset++] & 0xFF;
        final long byte96 = blocks[blocksOffset++] & 0xFF;
        final long byte97 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte92 & 63) << 35) | (byte93 << 27) | (byte94 << 19) | (byte95 << 11) | (byte96 << 3) | (byte97 >>> 5);
        final long byte98 = blocks[blocksOffset++] & 0xFF;
        final long byte99 = blocks[blocksOffset++] & 0xFF;
        final long byte100 = blocks[blocksOffset++] & 0xFF;
        final long byte101 = blocks[blocksOffset++] & 0xFF;
        final long byte102 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte97 & 31) << 36) | (byte98 << 28) | (byte99 << 20) | (byte100 << 12) | (byte101 << 4) | (byte102 >>> 4);
        final long byte103 = blocks[blocksOffset++] & 0xFF;
        final long byte104 = blocks[blocksOffset++] & 0xFF;
        final long byte105 = blocks[blocksOffset++] & 0xFF;
        final long byte106 = blocks[blocksOffset++] & 0xFF;
        final long byte107 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte102 & 15) << 37) | (byte103 << 29) | (byte104 << 21) | (byte105 << 13) | (byte106 << 5) | (byte107 >>> 3);
        final long byte108 = blocks[blocksOffset++] & 0xFF;
        final long byte109 = blocks[blocksOffset++] & 0xFF;
        final long byte110 = blocks[blocksOffset++] & 0xFF;
        final long byte111 = blocks[blocksOffset++] & 0xFF;
        final long byte112 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte107 & 7) << 38) | (byte108 << 30) | (byte109 << 22) | (byte110 << 14) | (byte111 << 6) | (byte112 >>> 2);
        final long byte113 = blocks[blocksOffset++] & 0xFF;
        final long byte114 = blocks[blocksOffset++] & 0xFF;
        final long byte115 = blocks[blocksOffset++] & 0xFF;
        final long byte116 = blocks[blocksOffset++] & 0xFF;
        final long byte117 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte112 & 3) << 39) | (byte113 << 31) | (byte114 << 23) | (byte115 << 15) | (byte116 << 7) | (byte117 >>> 1);
        final long byte118 = blocks[blocksOffset++] & 0xFF;
        final long byte119 = blocks[blocksOffset++] & 0xFF;
        final long byte120 = blocks[blocksOffset++] & 0xFF;
        final long byte121 = blocks[blocksOffset++] & 0xFF;
        final long byte122 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte117 & 1) << 40) | (byte118 << 32) | (byte119 << 24) | (byte120 << 16) | (byte121 << 8) | byte122;
        final long byte123 = blocks[blocksOffset++] & 0xFF;
        final long byte124 = blocks[blocksOffset++] & 0xFF;
        final long byte125 = blocks[blocksOffset++] & 0xFF;
        final long byte126 = blocks[blocksOffset++] & 0xFF;
        final long byte127 = blocks[blocksOffset++] & 0xFF;
        final long byte128 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = (byte123 << 33) | (byte124 << 25) | (byte125 << 17) | (byte126 << 9) | (byte127 << 1) | (byte128 >>> 7);
        final long byte129 = blocks[blocksOffset++] & 0xFF;
        final long byte130 = blocks[blocksOffset++] & 0xFF;
        final long byte131 = blocks[blocksOffset++] & 0xFF;
        final long byte132 = blocks[blocksOffset++] & 0xFF;
        final long byte133 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte128 & 127) << 34) | (byte129 << 26) | (byte130 << 18) | (byte131 << 10) | (byte132 << 2) | (byte133 >>> 6);
        final long byte134 = blocks[blocksOffset++] & 0xFF;
        final long byte135 = blocks[blocksOffset++] & 0xFF;
        final long byte136 = blocks[blocksOffset++] & 0xFF;
        final long byte137 = blocks[blocksOffset++] & 0xFF;
        final long byte138 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte133 & 63) << 35) | (byte134 << 27) | (byte135 << 19) | (byte136 << 11) | (byte137 << 3) | (byte138 >>> 5);
        final long byte139 = blocks[blocksOffset++] & 0xFF;
        final long byte140 = blocks[blocksOffset++] & 0xFF;
        final long byte141 = blocks[blocksOffset++] & 0xFF;
        final long byte142 = blocks[blocksOffset++] & 0xFF;
        final long byte143 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte138 & 31) << 36) | (byte139 << 28) | (byte140 << 20) | (byte141 << 12) | (byte142 << 4) | (byte143 >>> 4);
        final long byte144 = blocks[blocksOffset++] & 0xFF;
        final long byte145 = blocks[blocksOffset++] & 0xFF;
        final long byte146 = blocks[blocksOffset++] & 0xFF;
        final long byte147 = blocks[blocksOffset++] & 0xFF;
        final long byte148 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte143 & 15) << 37) | (byte144 << 29) | (byte145 << 21) | (byte146 << 13) | (byte147 << 5) | (byte148 >>> 3);
        final long byte149 = blocks[blocksOffset++] & 0xFF;
        final long byte150 = blocks[blocksOffset++] & 0xFF;
        final long byte151 = blocks[blocksOffset++] & 0xFF;
        final long byte152 = blocks[blocksOffset++] & 0xFF;
        final long byte153 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte148 & 7) << 38) | (byte149 << 30) | (byte150 << 22) | (byte151 << 14) | (byte152 << 6) | (byte153 >>> 2);
        final long byte154 = blocks[blocksOffset++] & 0xFF;
        final long byte155 = blocks[blocksOffset++] & 0xFF;
        final long byte156 = blocks[blocksOffset++] & 0xFF;
        final long byte157 = blocks[blocksOffset++] & 0xFF;
        final long byte158 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte153 & 3) << 39) | (byte154 << 31) | (byte155 << 23) | (byte156 << 15) | (byte157 << 7) | (byte158 >>> 1);
        final long byte159 = blocks[blocksOffset++] & 0xFF;
        final long byte160 = blocks[blocksOffset++] & 0xFF;
        final long byte161 = blocks[blocksOffset++] & 0xFF;
        final long byte162 = blocks[blocksOffset++] & 0xFF;
        final long byte163 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte158 & 1) << 40) | (byte159 << 32) | (byte160 << 24) | (byte161 << 16) | (byte162 << 8) | byte163;
        final long byte164 = blocks[blocksOffset++] & 0xFF;
        final long byte165 = blocks[blocksOffset++] & 0xFF;
        final long byte166 = blocks[blocksOffset++] & 0xFF;
        final long byte167 = blocks[blocksOffset++] & 0xFF;
        final long byte168 = blocks[blocksOffset++] & 0xFF;
        final long byte169 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = (byte164 << 33) | (byte165 << 25) | (byte166 << 17) | (byte167 << 9) | (byte168 << 1) | (byte169 >>> 7);
        final long byte170 = blocks[blocksOffset++] & 0xFF;
        final long byte171 = blocks[blocksOffset++] & 0xFF;
        final long byte172 = blocks[blocksOffset++] & 0xFF;
        final long byte173 = blocks[blocksOffset++] & 0xFF;
        final long byte174 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte169 & 127) << 34) | (byte170 << 26) | (byte171 << 18) | (byte172 << 10) | (byte173 << 2) | (byte174 >>> 6);
        final long byte175 = blocks[blocksOffset++] & 0xFF;
        final long byte176 = blocks[blocksOffset++] & 0xFF;
        final long byte177 = blocks[blocksOffset++] & 0xFF;
        final long byte178 = blocks[blocksOffset++] & 0xFF;
        final long byte179 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte174 & 63) << 35) | (byte175 << 27) | (byte176 << 19) | (byte177 << 11) | (byte178 << 3) | (byte179 >>> 5);
        final long byte180 = blocks[blocksOffset++] & 0xFF;
        final long byte181 = blocks[blocksOffset++] & 0xFF;
        final long byte182 = blocks[blocksOffset++] & 0xFF;
        final long byte183 = blocks[blocksOffset++] & 0xFF;
        final long byte184 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte179 & 31) << 36) | (byte180 << 28) | (byte181 << 20) | (byte182 << 12) | (byte183 << 4) | (byte184 >>> 4);
        final long byte185 = blocks[blocksOffset++] & 0xFF;
        final long byte186 = blocks[blocksOffset++] & 0xFF;
        final long byte187 = blocks[blocksOffset++] & 0xFF;
        final long byte188 = blocks[blocksOffset++] & 0xFF;
        final long byte189 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte184 & 15) << 37) | (byte185 << 29) | (byte186 << 21) | (byte187 << 13) | (byte188 << 5) | (byte189 >>> 3);
        final long byte190 = blocks[blocksOffset++] & 0xFF;
        final long byte191 = blocks[blocksOffset++] & 0xFF;
        final long byte192 = blocks[blocksOffset++] & 0xFF;
        final long byte193 = blocks[blocksOffset++] & 0xFF;
        final long byte194 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte189 & 7) << 38) | (byte190 << 30) | (byte191 << 22) | (byte192 << 14) | (byte193 << 6) | (byte194 >>> 2);
        final long byte195 = blocks[blocksOffset++] & 0xFF;
        final long byte196 = blocks[blocksOffset++] & 0xFF;
        final long byte197 = blocks[blocksOffset++] & 0xFF;
        final long byte198 = blocks[blocksOffset++] & 0xFF;
        final long byte199 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte194 & 3) << 39) | (byte195 << 31) | (byte196 << 23) | (byte197 << 15) | (byte198 << 7) | (byte199 >>> 1);
        final long byte200 = blocks[blocksOffset++] & 0xFF;
        final long byte201 = blocks[blocksOffset++] & 0xFF;
        final long byte202 = blocks[blocksOffset++] & 0xFF;
        final long byte203 = blocks[blocksOffset++] & 0xFF;
        final long byte204 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte199 & 1) << 40) | (byte200 << 32) | (byte201 << 24) | (byte202 << 16) | (byte203 << 8) | byte204;
        final long byte205 = blocks[blocksOffset++] & 0xFF;
        final long byte206 = blocks[blocksOffset++] & 0xFF;
        final long byte207 = blocks[blocksOffset++] & 0xFF;
        final long byte208 = blocks[blocksOffset++] & 0xFF;
        final long byte209 = blocks[blocksOffset++] & 0xFF;
        final long byte210 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = (byte205 << 33) | (byte206 << 25) | (byte207 << 17) | (byte208 << 9) | (byte209 << 1) | (byte210 >>> 7);
        final long byte211 = blocks[blocksOffset++] & 0xFF;
        final long byte212 = blocks[blocksOffset++] & 0xFF;
        final long byte213 = blocks[blocksOffset++] & 0xFF;
        final long byte214 = blocks[blocksOffset++] & 0xFF;
        final long byte215 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte210 & 127) << 34) | (byte211 << 26) | (byte212 << 18) | (byte213 << 10) | (byte214 << 2) | (byte215 >>> 6);
        final long byte216 = blocks[blocksOffset++] & 0xFF;
        final long byte217 = blocks[blocksOffset++] & 0xFF;
        final long byte218 = blocks[blocksOffset++] & 0xFF;
        final long byte219 = blocks[blocksOffset++] & 0xFF;
        final long byte220 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte215 & 63) << 35) | (byte216 << 27) | (byte217 << 19) | (byte218 << 11) | (byte219 << 3) | (byte220 >>> 5);
        final long byte221 = blocks[blocksOffset++] & 0xFF;
        final long byte222 = blocks[blocksOffset++] & 0xFF;
        final long byte223 = blocks[blocksOffset++] & 0xFF;
        final long byte224 = blocks[blocksOffset++] & 0xFF;
        final long byte225 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte220 & 31) << 36) | (byte221 << 28) | (byte222 << 20) | (byte223 << 12) | (byte224 << 4) | (byte225 >>> 4);
        final long byte226 = blocks[blocksOffset++] & 0xFF;
        final long byte227 = blocks[blocksOffset++] & 0xFF;
        final long byte228 = blocks[blocksOffset++] & 0xFF;
        final long byte229 = blocks[blocksOffset++] & 0xFF;
        final long byte230 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte225 & 15) << 37) | (byte226 << 29) | (byte227 << 21) | (byte228 << 13) | (byte229 << 5) | (byte230 >>> 3);
        final long byte231 = blocks[blocksOffset++] & 0xFF;
        final long byte232 = blocks[blocksOffset++] & 0xFF;
        final long byte233 = blocks[blocksOffset++] & 0xFF;
        final long byte234 = blocks[blocksOffset++] & 0xFF;
        final long byte235 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte230 & 7) << 38) | (byte231 << 30) | (byte232 << 22) | (byte233 << 14) | (byte234 << 6) | (byte235 >>> 2);
        final long byte236 = blocks[blocksOffset++] & 0xFF;
        final long byte237 = blocks[blocksOffset++] & 0xFF;
        final long byte238 = blocks[blocksOffset++] & 0xFF;
        final long byte239 = blocks[blocksOffset++] & 0xFF;
        final long byte240 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte235 & 3) << 39) | (byte236 << 31) | (byte237 << 23) | (byte238 << 15) | (byte239 << 7) | (byte240 >>> 1);
        final long byte241 = blocks[blocksOffset++] & 0xFF;
        final long byte242 = blocks[blocksOffset++] & 0xFF;
        final long byte243 = blocks[blocksOffset++] & 0xFF;
        final long byte244 = blocks[blocksOffset++] & 0xFF;
        final long byte245 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte240 & 1) << 40) | (byte241 << 32) | (byte242 << 24) | (byte243 << 16) | (byte244 << 8) | byte245;
        final long byte246 = blocks[blocksOffset++] & 0xFF;
        final long byte247 = blocks[blocksOffset++] & 0xFF;
        final long byte248 = blocks[blocksOffset++] & 0xFF;
        final long byte249 = blocks[blocksOffset++] & 0xFF;
        final long byte250 = blocks[blocksOffset++] & 0xFF;
        final long byte251 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = (byte246 << 33) | (byte247 << 25) | (byte248 << 17) | (byte249 << 9) | (byte250 << 1) | (byte251 >>> 7);
        final long byte252 = blocks[blocksOffset++] & 0xFF;
        final long byte253 = blocks[blocksOffset++] & 0xFF;
        final long byte254 = blocks[blocksOffset++] & 0xFF;
        final long byte255 = blocks[blocksOffset++] & 0xFF;
        final long byte256 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte251 & 127) << 34) | (byte252 << 26) | (byte253 << 18) | (byte254 << 10) | (byte255 << 2) | (byte256 >>> 6);
        final long byte257 = blocks[blocksOffset++] & 0xFF;
        final long byte258 = blocks[blocksOffset++] & 0xFF;
        final long byte259 = blocks[blocksOffset++] & 0xFF;
        final long byte260 = blocks[blocksOffset++] & 0xFF;
        final long byte261 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte256 & 63) << 35) | (byte257 << 27) | (byte258 << 19) | (byte259 << 11) | (byte260 << 3) | (byte261 >>> 5);
        final long byte262 = blocks[blocksOffset++] & 0xFF;
        final long byte263 = blocks[blocksOffset++] & 0xFF;
        final long byte264 = blocks[blocksOffset++] & 0xFF;
        final long byte265 = blocks[blocksOffset++] & 0xFF;
        final long byte266 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte261 & 31) << 36) | (byte262 << 28) | (byte263 << 20) | (byte264 << 12) | (byte265 << 4) | (byte266 >>> 4);
        final long byte267 = blocks[blocksOffset++] & 0xFF;
        final long byte268 = blocks[blocksOffset++] & 0xFF;
        final long byte269 = blocks[blocksOffset++] & 0xFF;
        final long byte270 = blocks[blocksOffset++] & 0xFF;
        final long byte271 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte266 & 15) << 37) | (byte267 << 29) | (byte268 << 21) | (byte269 << 13) | (byte270 << 5) | (byte271 >>> 3);
        final long byte272 = blocks[blocksOffset++] & 0xFF;
        final long byte273 = blocks[blocksOffset++] & 0xFF;
        final long byte274 = blocks[blocksOffset++] & 0xFF;
        final long byte275 = blocks[blocksOffset++] & 0xFF;
        final long byte276 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte271 & 7) << 38) | (byte272 << 30) | (byte273 << 22) | (byte274 << 14) | (byte275 << 6) | (byte276 >>> 2);
        final long byte277 = blocks[blocksOffset++] & 0xFF;
        final long byte278 = blocks[blocksOffset++] & 0xFF;
        final long byte279 = blocks[blocksOffset++] & 0xFF;
        final long byte280 = blocks[blocksOffset++] & 0xFF;
        final long byte281 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte276 & 3) << 39) | (byte277 << 31) | (byte278 << 23) | (byte279 << 15) | (byte280 << 7) | (byte281 >>> 1);
        final long byte282 = blocks[blocksOffset++] & 0xFF;
        final long byte283 = blocks[blocksOffset++] & 0xFF;
        final long byte284 = blocks[blocksOffset++] & 0xFF;
        final long byte285 = blocks[blocksOffset++] & 0xFF;
        final long byte286 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte281 & 1) << 40) | (byte282 << 32) | (byte283 << 24) | (byte284 << 16) | (byte285 << 8) | byte286;
        final long byte287 = blocks[blocksOffset++] & 0xFF;
        final long byte288 = blocks[blocksOffset++] & 0xFF;
        final long byte289 = blocks[blocksOffset++] & 0xFF;
        final long byte290 = blocks[blocksOffset++] & 0xFF;
        final long byte291 = blocks[blocksOffset++] & 0xFF;
        final long byte292 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = (byte287 << 33) | (byte288 << 25) | (byte289 << 17) | (byte290 << 9) | (byte291 << 1) | (byte292 >>> 7);
        final long byte293 = blocks[blocksOffset++] & 0xFF;
        final long byte294 = blocks[blocksOffset++] & 0xFF;
        final long byte295 = blocks[blocksOffset++] & 0xFF;
        final long byte296 = blocks[blocksOffset++] & 0xFF;
        final long byte297 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte292 & 127) << 34) | (byte293 << 26) | (byte294 << 18) | (byte295 << 10) | (byte296 << 2) | (byte297 >>> 6);
        final long byte298 = blocks[blocksOffset++] & 0xFF;
        final long byte299 = blocks[blocksOffset++] & 0xFF;
        final long byte300 = blocks[blocksOffset++] & 0xFF;
        final long byte301 = blocks[blocksOffset++] & 0xFF;
        final long byte302 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte297 & 63) << 35) | (byte298 << 27) | (byte299 << 19) | (byte300 << 11) | (byte301 << 3) | (byte302 >>> 5);
        final long byte303 = blocks[blocksOffset++] & 0xFF;
        final long byte304 = blocks[blocksOffset++] & 0xFF;
        final long byte305 = blocks[blocksOffset++] & 0xFF;
        final long byte306 = blocks[blocksOffset++] & 0xFF;
        final long byte307 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte302 & 31) << 36) | (byte303 << 28) | (byte304 << 20) | (byte305 << 12) | (byte306 << 4) | (byte307 >>> 4);
        final long byte308 = blocks[blocksOffset++] & 0xFF;
        final long byte309 = blocks[blocksOffset++] & 0xFF;
        final long byte310 = blocks[blocksOffset++] & 0xFF;
        final long byte311 = blocks[blocksOffset++] & 0xFF;
        final long byte312 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte307 & 15) << 37) | (byte308 << 29) | (byte309 << 21) | (byte310 << 13) | (byte311 << 5) | (byte312 >>> 3);
        final long byte313 = blocks[blocksOffset++] & 0xFF;
        final long byte314 = blocks[blocksOffset++] & 0xFF;
        final long byte315 = blocks[blocksOffset++] & 0xFF;
        final long byte316 = blocks[blocksOffset++] & 0xFF;
        final long byte317 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte312 & 7) << 38) | (byte313 << 30) | (byte314 << 22) | (byte315 << 14) | (byte316 << 6) | (byte317 >>> 2);
        final long byte318 = blocks[blocksOffset++] & 0xFF;
        final long byte319 = blocks[blocksOffset++] & 0xFF;
        final long byte320 = blocks[blocksOffset++] & 0xFF;
        final long byte321 = blocks[blocksOffset++] & 0xFF;
        final long byte322 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte317 & 3) << 39) | (byte318 << 31) | (byte319 << 23) | (byte320 << 15) | (byte321 << 7) | (byte322 >>> 1);
        final long byte323 = blocks[blocksOffset++] & 0xFF;
        final long byte324 = blocks[blocksOffset++] & 0xFF;
        final long byte325 = blocks[blocksOffset++] & 0xFF;
        final long byte326 = blocks[blocksOffset++] & 0xFF;
        final long byte327 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte322 & 1) << 40) | (byte323 << 32) | (byte324 << 24) | (byte325 << 16) | (byte326 << 8) | byte327;
      }
    }

    @Override
    public void encode(int[] values, int valuesOffset, long[] blocks, int blocksOffset, int iterations) {
      assert blocksOffset + iterations * blockCount() <= blocks.length;
      assert valuesOffset + iterations * valueCount() <= values.length;
      for (int i = 0; i < iterations; ++i) {
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 23) | ((values[valuesOffset] & 0xffffffffL) >>> 18);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 46) | ((values[valuesOffset++] & 0xffffffffL) << 5) | ((values[valuesOffset] & 0xffffffffL) >>> 36);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 28) | ((values[valuesOffset] & 0xffffffffL) >>> 13);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 51) | ((values[valuesOffset++] & 0xffffffffL) << 10) | ((values[valuesOffset] & 0xffffffffL) >>> 31);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 33) | ((values[valuesOffset] & 0xffffffffL) >>> 8);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 56) | ((values[valuesOffset++] & 0xffffffffL) << 15) | ((values[valuesOffset] & 0xffffffffL) >>> 26);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 38) | ((values[valuesOffset] & 0xffffffffL) >>> 3);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 61) | ((values[valuesOffset++] & 0xffffffffL) << 20) | ((values[valuesOffset] & 0xffffffffL) >>> 21);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 43) | ((values[valuesOffset++] & 0xffffffffL) << 2) | ((values[valuesOffset] & 0xffffffffL) >>> 39);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 25) | ((values[valuesOffset] & 0xffffffffL) >>> 16);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 48) | ((values[valuesOffset++] & 0xffffffffL) << 7) | ((values[valuesOffset] & 0xffffffffL) >>> 34);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 30) | ((values[valuesOffset] & 0xffffffffL) >>> 11);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 53) | ((values[valuesOffset++] & 0xffffffffL) << 12) | ((values[valuesOffset] & 0xffffffffL) >>> 29);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 35) | ((values[valuesOffset] & 0xffffffffL) >>> 6);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 58) | ((values[valuesOffset++] & 0xffffffffL) << 17) | ((values[valuesOffset] & 0xffffffffL) >>> 24);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 40) | ((values[valuesOffset] & 0xffffffffL) >>> 1);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 63) | ((values[valuesOffset++] & 0xffffffffL) << 22) | ((values[valuesOffset] & 0xffffffffL) >>> 19);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 45) | ((values[valuesOffset++] & 0xffffffffL) << 4) | ((values[valuesOffset] & 0xffffffffL) >>> 37);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 27) | ((values[valuesOffset] & 0xffffffffL) >>> 14);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 50) | ((values[valuesOffset++] & 0xffffffffL) << 9) | ((values[valuesOffset] & 0xffffffffL) >>> 32);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 32) | ((values[valuesOffset] & 0xffffffffL) >>> 9);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 55) | ((values[valuesOffset++] & 0xffffffffL) << 14) | ((values[valuesOffset] & 0xffffffffL) >>> 27);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 37) | ((values[valuesOffset] & 0xffffffffL) >>> 4);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 60) | ((values[valuesOffset++] & 0xffffffffL) << 19) | ((values[valuesOffset] & 0xffffffffL) >>> 22);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 42) | ((values[valuesOffset++] & 0xffffffffL) << 1) | ((values[valuesOffset] & 0xffffffffL) >>> 40);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 24) | ((values[valuesOffset] & 0xffffffffL) >>> 17);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 47) | ((values[valuesOffset++] & 0xffffffffL) << 6) | ((values[valuesOffset] & 0xffffffffL) >>> 35);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 29) | ((values[valuesOffset] & 0xffffffffL) >>> 12);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 52) | ((values[valuesOffset++] & 0xffffffffL) << 11) | ((values[valuesOffset] & 0xffffffffL) >>> 30);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 34) | ((values[valuesOffset] & 0xffffffffL) >>> 7);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 57) | ((values[valuesOffset++] & 0xffffffffL) << 16) | ((values[valuesOffset] & 0xffffffffL) >>> 25);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 39) | ((values[valuesOffset] & 0xffffffffL) >>> 2);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 62) | ((values[valuesOffset++] & 0xffffffffL) << 21) | ((values[valuesOffset] & 0xffffffffL) >>> 20);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 44) | ((values[valuesOffset++] & 0xffffffffL) << 3) | ((values[valuesOffset] & 0xffffffffL) >>> 38);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 26) | ((values[valuesOffset] & 0xffffffffL) >>> 15);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 49) | ((values[valuesOffset++] & 0xffffffffL) << 8) | ((values[valuesOffset] & 0xffffffffL) >>> 33);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 31) | ((values[valuesOffset] & 0xffffffffL) >>> 10);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 54) | ((values[valuesOffset++] & 0xffffffffL) << 13) | ((values[valuesOffset] & 0xffffffffL) >>> 28);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 36) | ((values[valuesOffset] & 0xffffffffL) >>> 5);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 59) | ((values[valuesOffset++] & 0xffffffffL) << 18) | ((values[valuesOffset] & 0xffffffffL) >>> 23);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 41) | (values[valuesOffset++] & 0xffffffffL);
      }
    }

    @Override
    public void encode(long[] values, int valuesOffset, long[] blocks, int blocksOffset, int iterations) {
      assert blocksOffset + iterations * blockCount() <= blocks.length;
      assert valuesOffset + iterations * valueCount() <= values.length;
      for (int i = 0; i < iterations; ++i) {
        blocks[blocksOffset++] = (values[valuesOffset++] << 23) | (values[valuesOffset] >>> 18);
        blocks[blocksOffset++] = (values[valuesOffset++] << 46) | (values[valuesOffset++] << 5) | (values[valuesOffset] >>> 36);
        blocks[blocksOffset++] = (values[valuesOffset++] << 28) | (values[valuesOffset] >>> 13);
        blocks[blocksOffset++] = (values[valuesOffset++] << 51) | (values[valuesOffset++] << 10) | (values[valuesOffset] >>> 31);
        blocks[blocksOffset++] = (values[valuesOffset++] << 33) | (values[valuesOffset] >>> 8);
        blocks[blocksOffset++] = (values[valuesOffset++] << 56) | (values[valuesOffset++] << 15) | (values[valuesOffset] >>> 26);
        blocks[blocksOffset++] = (values[valuesOffset++] << 38) | (values[valuesOffset] >>> 3);
        blocks[blocksOffset++] = (values[valuesOffset++] << 61) | (values[valuesOffset++] << 20) | (values[valuesOffset] >>> 21);
        blocks[blocksOffset++] = (values[valuesOffset++] << 43) | (values[valuesOffset++] << 2) | (values[valuesOffset] >>> 39);
        blocks[blocksOffset++] = (values[valuesOffset++] << 25) | (values[valuesOffset] >>> 16);
        blocks[blocksOffset++] = (values[valuesOffset++] << 48) | (values[valuesOffset++] << 7) | (values[valuesOffset] >>> 34);
        blocks[blocksOffset++] = (values[valuesOffset++] << 30) | (values[valuesOffset] >>> 11);
        blocks[blocksOffset++] = (values[valuesOffset++] << 53) | (values[valuesOffset++] << 12) | (values[valuesOffset] >>> 29);
        blocks[blocksOffset++] = (values[valuesOffset++] << 35) | (values[valuesOffset] >>> 6);
        blocks[blocksOffset++] = (values[valuesOffset++] << 58) | (values[valuesOffset++] << 17) | (values[valuesOffset] >>> 24);
        blocks[blocksOffset++] = (values[valuesOffset++] << 40) | (values[valuesOffset] >>> 1);
        blocks[blocksOffset++] = (values[valuesOffset++] << 63) | (values[valuesOffset++] << 22) | (values[valuesOffset] >>> 19);
        blocks[blocksOffset++] = (values[valuesOffset++] << 45) | (values[valuesOffset++] << 4) | (values[valuesOffset] >>> 37);
        blocks[blocksOffset++] = (values[valuesOffset++] << 27) | (values[valuesOffset] >>> 14);
        blocks[blocksOffset++] = (values[valuesOffset++] << 50) | (values[valuesOffset++] << 9) | (values[valuesOffset] >>> 32);
        blocks[blocksOffset++] = (values[valuesOffset++] << 32) | (values[valuesOffset] >>> 9);
        blocks[blocksOffset++] = (values[valuesOffset++] << 55) | (values[valuesOffset++] << 14) | (values[valuesOffset] >>> 27);
        blocks[blocksOffset++] = (values[valuesOffset++] << 37) | (values[valuesOffset] >>> 4);
        blocks[blocksOffset++] = (values[valuesOffset++] << 60) | (values[valuesOffset++] << 19) | (values[valuesOffset] >>> 22);
        blocks[blocksOffset++] = (values[valuesOffset++] << 42) | (values[valuesOffset++] << 1) | (values[valuesOffset] >>> 40);
        blocks[blocksOffset++] = (values[valuesOffset++] << 24) | (values[valuesOffset] >>> 17);
        blocks[blocksOffset++] = (values[valuesOffset++] << 47) | (values[valuesOffset++] << 6) | (values[valuesOffset] >>> 35);
        blocks[blocksOffset++] = (values[valuesOffset++] << 29) | (values[valuesOffset] >>> 12);
        blocks[blocksOffset++] = (values[valuesOffset++] << 52) | (values[valuesOffset++] << 11) | (values[valuesOffset] >>> 30);
        blocks[blocksOffset++] = (values[valuesOffset++] << 34) | (values[valuesOffset] >>> 7);
        blocks[blocksOffset++] = (values[valuesOffset++] << 57) | (values[valuesOffset++] << 16) | (values[valuesOffset] >>> 25);
        blocks[blocksOffset++] = (values[valuesOffset++] << 39) | (values[valuesOffset] >>> 2);
        blocks[blocksOffset++] = (values[valuesOffset++] << 62) | (values[valuesOffset++] << 21) | (values[valuesOffset] >>> 20);
        blocks[blocksOffset++] = (values[valuesOffset++] << 44) | (values[valuesOffset++] << 3) | (values[valuesOffset] >>> 38);
        blocks[blocksOffset++] = (values[valuesOffset++] << 26) | (values[valuesOffset] >>> 15);
        blocks[blocksOffset++] = (values[valuesOffset++] << 49) | (values[valuesOffset++] << 8) | (values[valuesOffset] >>> 33);
        blocks[blocksOffset++] = (values[valuesOffset++] << 31) | (values[valuesOffset] >>> 10);
        blocks[blocksOffset++] = (values[valuesOffset++] << 54) | (values[valuesOffset++] << 13) | (values[valuesOffset] >>> 28);
        blocks[blocksOffset++] = (values[valuesOffset++] << 36) | (values[valuesOffset] >>> 5);
        blocks[blocksOffset++] = (values[valuesOffset++] << 59) | (values[valuesOffset++] << 18) | (values[valuesOffset] >>> 23);
        blocks[blocksOffset++] = (values[valuesOffset++] << 41) | values[valuesOffset++];
      }
    }

}
