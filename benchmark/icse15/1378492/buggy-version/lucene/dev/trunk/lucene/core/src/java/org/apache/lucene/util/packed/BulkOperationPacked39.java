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
final class BulkOperationPacked39 extends BulkOperation {
    @Override
    public int blockCount() {
      return 39;
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
        values[valuesOffset++] = block0 >>> 25;
        final long block1 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block0 & 33554431L) << 14) | (block1 >>> 50);
        values[valuesOffset++] = (block1 >>> 11) & 549755813887L;
        final long block2 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block1 & 2047L) << 28) | (block2 >>> 36);
        final long block3 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block2 & 68719476735L) << 3) | (block3 >>> 61);
        values[valuesOffset++] = (block3 >>> 22) & 549755813887L;
        final long block4 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block3 & 4194303L) << 17) | (block4 >>> 47);
        values[valuesOffset++] = (block4 >>> 8) & 549755813887L;
        final long block5 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block4 & 255L) << 31) | (block5 >>> 33);
        final long block6 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block5 & 8589934591L) << 6) | (block6 >>> 58);
        values[valuesOffset++] = (block6 >>> 19) & 549755813887L;
        final long block7 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block6 & 524287L) << 20) | (block7 >>> 44);
        values[valuesOffset++] = (block7 >>> 5) & 549755813887L;
        final long block8 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block7 & 31L) << 34) | (block8 >>> 30);
        final long block9 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block8 & 1073741823L) << 9) | (block9 >>> 55);
        values[valuesOffset++] = (block9 >>> 16) & 549755813887L;
        final long block10 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block9 & 65535L) << 23) | (block10 >>> 41);
        values[valuesOffset++] = (block10 >>> 2) & 549755813887L;
        final long block11 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block10 & 3L) << 37) | (block11 >>> 27);
        final long block12 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block11 & 134217727L) << 12) | (block12 >>> 52);
        values[valuesOffset++] = (block12 >>> 13) & 549755813887L;
        final long block13 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block12 & 8191L) << 26) | (block13 >>> 38);
        final long block14 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block13 & 274877906943L) << 1) | (block14 >>> 63);
        values[valuesOffset++] = (block14 >>> 24) & 549755813887L;
        final long block15 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block14 & 16777215L) << 15) | (block15 >>> 49);
        values[valuesOffset++] = (block15 >>> 10) & 549755813887L;
        final long block16 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block15 & 1023L) << 29) | (block16 >>> 35);
        final long block17 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block16 & 34359738367L) << 4) | (block17 >>> 60);
        values[valuesOffset++] = (block17 >>> 21) & 549755813887L;
        final long block18 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block17 & 2097151L) << 18) | (block18 >>> 46);
        values[valuesOffset++] = (block18 >>> 7) & 549755813887L;
        final long block19 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block18 & 127L) << 32) | (block19 >>> 32);
        final long block20 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block19 & 4294967295L) << 7) | (block20 >>> 57);
        values[valuesOffset++] = (block20 >>> 18) & 549755813887L;
        final long block21 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block20 & 262143L) << 21) | (block21 >>> 43);
        values[valuesOffset++] = (block21 >>> 4) & 549755813887L;
        final long block22 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block21 & 15L) << 35) | (block22 >>> 29);
        final long block23 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block22 & 536870911L) << 10) | (block23 >>> 54);
        values[valuesOffset++] = (block23 >>> 15) & 549755813887L;
        final long block24 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block23 & 32767L) << 24) | (block24 >>> 40);
        values[valuesOffset++] = (block24 >>> 1) & 549755813887L;
        final long block25 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block24 & 1L) << 38) | (block25 >>> 26);
        final long block26 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block25 & 67108863L) << 13) | (block26 >>> 51);
        values[valuesOffset++] = (block26 >>> 12) & 549755813887L;
        final long block27 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block26 & 4095L) << 27) | (block27 >>> 37);
        final long block28 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block27 & 137438953471L) << 2) | (block28 >>> 62);
        values[valuesOffset++] = (block28 >>> 23) & 549755813887L;
        final long block29 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block28 & 8388607L) << 16) | (block29 >>> 48);
        values[valuesOffset++] = (block29 >>> 9) & 549755813887L;
        final long block30 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block29 & 511L) << 30) | (block30 >>> 34);
        final long block31 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block30 & 17179869183L) << 5) | (block31 >>> 59);
        values[valuesOffset++] = (block31 >>> 20) & 549755813887L;
        final long block32 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block31 & 1048575L) << 19) | (block32 >>> 45);
        values[valuesOffset++] = (block32 >>> 6) & 549755813887L;
        final long block33 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block32 & 63L) << 33) | (block33 >>> 31);
        final long block34 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block33 & 2147483647L) << 8) | (block34 >>> 56);
        values[valuesOffset++] = (block34 >>> 17) & 549755813887L;
        final long block35 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block34 & 131071L) << 22) | (block35 >>> 42);
        values[valuesOffset++] = (block35 >>> 3) & 549755813887L;
        final long block36 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block35 & 7L) << 36) | (block36 >>> 28);
        final long block37 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block36 & 268435455L) << 11) | (block37 >>> 53);
        values[valuesOffset++] = (block37 >>> 14) & 549755813887L;
        final long block38 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block37 & 16383L) << 25) | (block38 >>> 39);
        values[valuesOffset++] = block38 & 549755813887L;
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
        values[valuesOffset++] = (byte0 << 31) | (byte1 << 23) | (byte2 << 15) | (byte3 << 7) | (byte4 >>> 1);
        final long byte5 = blocks[blocksOffset++] & 0xFF;
        final long byte6 = blocks[blocksOffset++] & 0xFF;
        final long byte7 = blocks[blocksOffset++] & 0xFF;
        final long byte8 = blocks[blocksOffset++] & 0xFF;
        final long byte9 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte4 & 1) << 38) | (byte5 << 30) | (byte6 << 22) | (byte7 << 14) | (byte8 << 6) | (byte9 >>> 2);
        final long byte10 = blocks[blocksOffset++] & 0xFF;
        final long byte11 = blocks[blocksOffset++] & 0xFF;
        final long byte12 = blocks[blocksOffset++] & 0xFF;
        final long byte13 = blocks[blocksOffset++] & 0xFF;
        final long byte14 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte9 & 3) << 37) | (byte10 << 29) | (byte11 << 21) | (byte12 << 13) | (byte13 << 5) | (byte14 >>> 3);
        final long byte15 = blocks[blocksOffset++] & 0xFF;
        final long byte16 = blocks[blocksOffset++] & 0xFF;
        final long byte17 = blocks[blocksOffset++] & 0xFF;
        final long byte18 = blocks[blocksOffset++] & 0xFF;
        final long byte19 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte14 & 7) << 36) | (byte15 << 28) | (byte16 << 20) | (byte17 << 12) | (byte18 << 4) | (byte19 >>> 4);
        final long byte20 = blocks[blocksOffset++] & 0xFF;
        final long byte21 = blocks[blocksOffset++] & 0xFF;
        final long byte22 = blocks[blocksOffset++] & 0xFF;
        final long byte23 = blocks[blocksOffset++] & 0xFF;
        final long byte24 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte19 & 15) << 35) | (byte20 << 27) | (byte21 << 19) | (byte22 << 11) | (byte23 << 3) | (byte24 >>> 5);
        final long byte25 = blocks[blocksOffset++] & 0xFF;
        final long byte26 = blocks[blocksOffset++] & 0xFF;
        final long byte27 = blocks[blocksOffset++] & 0xFF;
        final long byte28 = blocks[blocksOffset++] & 0xFF;
        final long byte29 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte24 & 31) << 34) | (byte25 << 26) | (byte26 << 18) | (byte27 << 10) | (byte28 << 2) | (byte29 >>> 6);
        final long byte30 = blocks[blocksOffset++] & 0xFF;
        final long byte31 = blocks[blocksOffset++] & 0xFF;
        final long byte32 = blocks[blocksOffset++] & 0xFF;
        final long byte33 = blocks[blocksOffset++] & 0xFF;
        final long byte34 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte29 & 63) << 33) | (byte30 << 25) | (byte31 << 17) | (byte32 << 9) | (byte33 << 1) | (byte34 >>> 7);
        final long byte35 = blocks[blocksOffset++] & 0xFF;
        final long byte36 = blocks[blocksOffset++] & 0xFF;
        final long byte37 = blocks[blocksOffset++] & 0xFF;
        final long byte38 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte34 & 127) << 32) | (byte35 << 24) | (byte36 << 16) | (byte37 << 8) | byte38;
        final long byte39 = blocks[blocksOffset++] & 0xFF;
        final long byte40 = blocks[blocksOffset++] & 0xFF;
        final long byte41 = blocks[blocksOffset++] & 0xFF;
        final long byte42 = blocks[blocksOffset++] & 0xFF;
        final long byte43 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = (byte39 << 31) | (byte40 << 23) | (byte41 << 15) | (byte42 << 7) | (byte43 >>> 1);
        final long byte44 = blocks[blocksOffset++] & 0xFF;
        final long byte45 = blocks[blocksOffset++] & 0xFF;
        final long byte46 = blocks[blocksOffset++] & 0xFF;
        final long byte47 = blocks[blocksOffset++] & 0xFF;
        final long byte48 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte43 & 1) << 38) | (byte44 << 30) | (byte45 << 22) | (byte46 << 14) | (byte47 << 6) | (byte48 >>> 2);
        final long byte49 = blocks[blocksOffset++] & 0xFF;
        final long byte50 = blocks[blocksOffset++] & 0xFF;
        final long byte51 = blocks[blocksOffset++] & 0xFF;
        final long byte52 = blocks[blocksOffset++] & 0xFF;
        final long byte53 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte48 & 3) << 37) | (byte49 << 29) | (byte50 << 21) | (byte51 << 13) | (byte52 << 5) | (byte53 >>> 3);
        final long byte54 = blocks[blocksOffset++] & 0xFF;
        final long byte55 = blocks[blocksOffset++] & 0xFF;
        final long byte56 = blocks[blocksOffset++] & 0xFF;
        final long byte57 = blocks[blocksOffset++] & 0xFF;
        final long byte58 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte53 & 7) << 36) | (byte54 << 28) | (byte55 << 20) | (byte56 << 12) | (byte57 << 4) | (byte58 >>> 4);
        final long byte59 = blocks[blocksOffset++] & 0xFF;
        final long byte60 = blocks[blocksOffset++] & 0xFF;
        final long byte61 = blocks[blocksOffset++] & 0xFF;
        final long byte62 = blocks[blocksOffset++] & 0xFF;
        final long byte63 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte58 & 15) << 35) | (byte59 << 27) | (byte60 << 19) | (byte61 << 11) | (byte62 << 3) | (byte63 >>> 5);
        final long byte64 = blocks[blocksOffset++] & 0xFF;
        final long byte65 = blocks[blocksOffset++] & 0xFF;
        final long byte66 = blocks[blocksOffset++] & 0xFF;
        final long byte67 = blocks[blocksOffset++] & 0xFF;
        final long byte68 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte63 & 31) << 34) | (byte64 << 26) | (byte65 << 18) | (byte66 << 10) | (byte67 << 2) | (byte68 >>> 6);
        final long byte69 = blocks[blocksOffset++] & 0xFF;
        final long byte70 = blocks[blocksOffset++] & 0xFF;
        final long byte71 = blocks[blocksOffset++] & 0xFF;
        final long byte72 = blocks[blocksOffset++] & 0xFF;
        final long byte73 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte68 & 63) << 33) | (byte69 << 25) | (byte70 << 17) | (byte71 << 9) | (byte72 << 1) | (byte73 >>> 7);
        final long byte74 = blocks[blocksOffset++] & 0xFF;
        final long byte75 = blocks[blocksOffset++] & 0xFF;
        final long byte76 = blocks[blocksOffset++] & 0xFF;
        final long byte77 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte73 & 127) << 32) | (byte74 << 24) | (byte75 << 16) | (byte76 << 8) | byte77;
        final long byte78 = blocks[blocksOffset++] & 0xFF;
        final long byte79 = blocks[blocksOffset++] & 0xFF;
        final long byte80 = blocks[blocksOffset++] & 0xFF;
        final long byte81 = blocks[blocksOffset++] & 0xFF;
        final long byte82 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = (byte78 << 31) | (byte79 << 23) | (byte80 << 15) | (byte81 << 7) | (byte82 >>> 1);
        final long byte83 = blocks[blocksOffset++] & 0xFF;
        final long byte84 = blocks[blocksOffset++] & 0xFF;
        final long byte85 = blocks[blocksOffset++] & 0xFF;
        final long byte86 = blocks[blocksOffset++] & 0xFF;
        final long byte87 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte82 & 1) << 38) | (byte83 << 30) | (byte84 << 22) | (byte85 << 14) | (byte86 << 6) | (byte87 >>> 2);
        final long byte88 = blocks[blocksOffset++] & 0xFF;
        final long byte89 = blocks[blocksOffset++] & 0xFF;
        final long byte90 = blocks[blocksOffset++] & 0xFF;
        final long byte91 = blocks[blocksOffset++] & 0xFF;
        final long byte92 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte87 & 3) << 37) | (byte88 << 29) | (byte89 << 21) | (byte90 << 13) | (byte91 << 5) | (byte92 >>> 3);
        final long byte93 = blocks[blocksOffset++] & 0xFF;
        final long byte94 = blocks[blocksOffset++] & 0xFF;
        final long byte95 = blocks[blocksOffset++] & 0xFF;
        final long byte96 = blocks[blocksOffset++] & 0xFF;
        final long byte97 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte92 & 7) << 36) | (byte93 << 28) | (byte94 << 20) | (byte95 << 12) | (byte96 << 4) | (byte97 >>> 4);
        final long byte98 = blocks[blocksOffset++] & 0xFF;
        final long byte99 = blocks[blocksOffset++] & 0xFF;
        final long byte100 = blocks[blocksOffset++] & 0xFF;
        final long byte101 = blocks[blocksOffset++] & 0xFF;
        final long byte102 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte97 & 15) << 35) | (byte98 << 27) | (byte99 << 19) | (byte100 << 11) | (byte101 << 3) | (byte102 >>> 5);
        final long byte103 = blocks[blocksOffset++] & 0xFF;
        final long byte104 = blocks[blocksOffset++] & 0xFF;
        final long byte105 = blocks[blocksOffset++] & 0xFF;
        final long byte106 = blocks[blocksOffset++] & 0xFF;
        final long byte107 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte102 & 31) << 34) | (byte103 << 26) | (byte104 << 18) | (byte105 << 10) | (byte106 << 2) | (byte107 >>> 6);
        final long byte108 = blocks[blocksOffset++] & 0xFF;
        final long byte109 = blocks[blocksOffset++] & 0xFF;
        final long byte110 = blocks[blocksOffset++] & 0xFF;
        final long byte111 = blocks[blocksOffset++] & 0xFF;
        final long byte112 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte107 & 63) << 33) | (byte108 << 25) | (byte109 << 17) | (byte110 << 9) | (byte111 << 1) | (byte112 >>> 7);
        final long byte113 = blocks[blocksOffset++] & 0xFF;
        final long byte114 = blocks[blocksOffset++] & 0xFF;
        final long byte115 = blocks[blocksOffset++] & 0xFF;
        final long byte116 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte112 & 127) << 32) | (byte113 << 24) | (byte114 << 16) | (byte115 << 8) | byte116;
        final long byte117 = blocks[blocksOffset++] & 0xFF;
        final long byte118 = blocks[blocksOffset++] & 0xFF;
        final long byte119 = blocks[blocksOffset++] & 0xFF;
        final long byte120 = blocks[blocksOffset++] & 0xFF;
        final long byte121 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = (byte117 << 31) | (byte118 << 23) | (byte119 << 15) | (byte120 << 7) | (byte121 >>> 1);
        final long byte122 = blocks[blocksOffset++] & 0xFF;
        final long byte123 = blocks[blocksOffset++] & 0xFF;
        final long byte124 = blocks[blocksOffset++] & 0xFF;
        final long byte125 = blocks[blocksOffset++] & 0xFF;
        final long byte126 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte121 & 1) << 38) | (byte122 << 30) | (byte123 << 22) | (byte124 << 14) | (byte125 << 6) | (byte126 >>> 2);
        final long byte127 = blocks[blocksOffset++] & 0xFF;
        final long byte128 = blocks[blocksOffset++] & 0xFF;
        final long byte129 = blocks[blocksOffset++] & 0xFF;
        final long byte130 = blocks[blocksOffset++] & 0xFF;
        final long byte131 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte126 & 3) << 37) | (byte127 << 29) | (byte128 << 21) | (byte129 << 13) | (byte130 << 5) | (byte131 >>> 3);
        final long byte132 = blocks[blocksOffset++] & 0xFF;
        final long byte133 = blocks[blocksOffset++] & 0xFF;
        final long byte134 = blocks[blocksOffset++] & 0xFF;
        final long byte135 = blocks[blocksOffset++] & 0xFF;
        final long byte136 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte131 & 7) << 36) | (byte132 << 28) | (byte133 << 20) | (byte134 << 12) | (byte135 << 4) | (byte136 >>> 4);
        final long byte137 = blocks[blocksOffset++] & 0xFF;
        final long byte138 = blocks[blocksOffset++] & 0xFF;
        final long byte139 = blocks[blocksOffset++] & 0xFF;
        final long byte140 = blocks[blocksOffset++] & 0xFF;
        final long byte141 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte136 & 15) << 35) | (byte137 << 27) | (byte138 << 19) | (byte139 << 11) | (byte140 << 3) | (byte141 >>> 5);
        final long byte142 = blocks[blocksOffset++] & 0xFF;
        final long byte143 = blocks[blocksOffset++] & 0xFF;
        final long byte144 = blocks[blocksOffset++] & 0xFF;
        final long byte145 = blocks[blocksOffset++] & 0xFF;
        final long byte146 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte141 & 31) << 34) | (byte142 << 26) | (byte143 << 18) | (byte144 << 10) | (byte145 << 2) | (byte146 >>> 6);
        final long byte147 = blocks[blocksOffset++] & 0xFF;
        final long byte148 = blocks[blocksOffset++] & 0xFF;
        final long byte149 = blocks[blocksOffset++] & 0xFF;
        final long byte150 = blocks[blocksOffset++] & 0xFF;
        final long byte151 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte146 & 63) << 33) | (byte147 << 25) | (byte148 << 17) | (byte149 << 9) | (byte150 << 1) | (byte151 >>> 7);
        final long byte152 = blocks[blocksOffset++] & 0xFF;
        final long byte153 = blocks[blocksOffset++] & 0xFF;
        final long byte154 = blocks[blocksOffset++] & 0xFF;
        final long byte155 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte151 & 127) << 32) | (byte152 << 24) | (byte153 << 16) | (byte154 << 8) | byte155;
        final long byte156 = blocks[blocksOffset++] & 0xFF;
        final long byte157 = blocks[blocksOffset++] & 0xFF;
        final long byte158 = blocks[blocksOffset++] & 0xFF;
        final long byte159 = blocks[blocksOffset++] & 0xFF;
        final long byte160 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = (byte156 << 31) | (byte157 << 23) | (byte158 << 15) | (byte159 << 7) | (byte160 >>> 1);
        final long byte161 = blocks[blocksOffset++] & 0xFF;
        final long byte162 = blocks[blocksOffset++] & 0xFF;
        final long byte163 = blocks[blocksOffset++] & 0xFF;
        final long byte164 = blocks[blocksOffset++] & 0xFF;
        final long byte165 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte160 & 1) << 38) | (byte161 << 30) | (byte162 << 22) | (byte163 << 14) | (byte164 << 6) | (byte165 >>> 2);
        final long byte166 = blocks[blocksOffset++] & 0xFF;
        final long byte167 = blocks[blocksOffset++] & 0xFF;
        final long byte168 = blocks[blocksOffset++] & 0xFF;
        final long byte169 = blocks[blocksOffset++] & 0xFF;
        final long byte170 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte165 & 3) << 37) | (byte166 << 29) | (byte167 << 21) | (byte168 << 13) | (byte169 << 5) | (byte170 >>> 3);
        final long byte171 = blocks[blocksOffset++] & 0xFF;
        final long byte172 = blocks[blocksOffset++] & 0xFF;
        final long byte173 = blocks[blocksOffset++] & 0xFF;
        final long byte174 = blocks[blocksOffset++] & 0xFF;
        final long byte175 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte170 & 7) << 36) | (byte171 << 28) | (byte172 << 20) | (byte173 << 12) | (byte174 << 4) | (byte175 >>> 4);
        final long byte176 = blocks[blocksOffset++] & 0xFF;
        final long byte177 = blocks[blocksOffset++] & 0xFF;
        final long byte178 = blocks[blocksOffset++] & 0xFF;
        final long byte179 = blocks[blocksOffset++] & 0xFF;
        final long byte180 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte175 & 15) << 35) | (byte176 << 27) | (byte177 << 19) | (byte178 << 11) | (byte179 << 3) | (byte180 >>> 5);
        final long byte181 = blocks[blocksOffset++] & 0xFF;
        final long byte182 = blocks[blocksOffset++] & 0xFF;
        final long byte183 = blocks[blocksOffset++] & 0xFF;
        final long byte184 = blocks[blocksOffset++] & 0xFF;
        final long byte185 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte180 & 31) << 34) | (byte181 << 26) | (byte182 << 18) | (byte183 << 10) | (byte184 << 2) | (byte185 >>> 6);
        final long byte186 = blocks[blocksOffset++] & 0xFF;
        final long byte187 = blocks[blocksOffset++] & 0xFF;
        final long byte188 = blocks[blocksOffset++] & 0xFF;
        final long byte189 = blocks[blocksOffset++] & 0xFF;
        final long byte190 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte185 & 63) << 33) | (byte186 << 25) | (byte187 << 17) | (byte188 << 9) | (byte189 << 1) | (byte190 >>> 7);
        final long byte191 = blocks[blocksOffset++] & 0xFF;
        final long byte192 = blocks[blocksOffset++] & 0xFF;
        final long byte193 = blocks[blocksOffset++] & 0xFF;
        final long byte194 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte190 & 127) << 32) | (byte191 << 24) | (byte192 << 16) | (byte193 << 8) | byte194;
        final long byte195 = blocks[blocksOffset++] & 0xFF;
        final long byte196 = blocks[blocksOffset++] & 0xFF;
        final long byte197 = blocks[blocksOffset++] & 0xFF;
        final long byte198 = blocks[blocksOffset++] & 0xFF;
        final long byte199 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = (byte195 << 31) | (byte196 << 23) | (byte197 << 15) | (byte198 << 7) | (byte199 >>> 1);
        final long byte200 = blocks[blocksOffset++] & 0xFF;
        final long byte201 = blocks[blocksOffset++] & 0xFF;
        final long byte202 = blocks[blocksOffset++] & 0xFF;
        final long byte203 = blocks[blocksOffset++] & 0xFF;
        final long byte204 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte199 & 1) << 38) | (byte200 << 30) | (byte201 << 22) | (byte202 << 14) | (byte203 << 6) | (byte204 >>> 2);
        final long byte205 = blocks[blocksOffset++] & 0xFF;
        final long byte206 = blocks[blocksOffset++] & 0xFF;
        final long byte207 = blocks[blocksOffset++] & 0xFF;
        final long byte208 = blocks[blocksOffset++] & 0xFF;
        final long byte209 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte204 & 3) << 37) | (byte205 << 29) | (byte206 << 21) | (byte207 << 13) | (byte208 << 5) | (byte209 >>> 3);
        final long byte210 = blocks[blocksOffset++] & 0xFF;
        final long byte211 = blocks[blocksOffset++] & 0xFF;
        final long byte212 = blocks[blocksOffset++] & 0xFF;
        final long byte213 = blocks[blocksOffset++] & 0xFF;
        final long byte214 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte209 & 7) << 36) | (byte210 << 28) | (byte211 << 20) | (byte212 << 12) | (byte213 << 4) | (byte214 >>> 4);
        final long byte215 = blocks[blocksOffset++] & 0xFF;
        final long byte216 = blocks[blocksOffset++] & 0xFF;
        final long byte217 = blocks[blocksOffset++] & 0xFF;
        final long byte218 = blocks[blocksOffset++] & 0xFF;
        final long byte219 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte214 & 15) << 35) | (byte215 << 27) | (byte216 << 19) | (byte217 << 11) | (byte218 << 3) | (byte219 >>> 5);
        final long byte220 = blocks[blocksOffset++] & 0xFF;
        final long byte221 = blocks[blocksOffset++] & 0xFF;
        final long byte222 = blocks[blocksOffset++] & 0xFF;
        final long byte223 = blocks[blocksOffset++] & 0xFF;
        final long byte224 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte219 & 31) << 34) | (byte220 << 26) | (byte221 << 18) | (byte222 << 10) | (byte223 << 2) | (byte224 >>> 6);
        final long byte225 = blocks[blocksOffset++] & 0xFF;
        final long byte226 = blocks[blocksOffset++] & 0xFF;
        final long byte227 = blocks[blocksOffset++] & 0xFF;
        final long byte228 = blocks[blocksOffset++] & 0xFF;
        final long byte229 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte224 & 63) << 33) | (byte225 << 25) | (byte226 << 17) | (byte227 << 9) | (byte228 << 1) | (byte229 >>> 7);
        final long byte230 = blocks[blocksOffset++] & 0xFF;
        final long byte231 = blocks[blocksOffset++] & 0xFF;
        final long byte232 = blocks[blocksOffset++] & 0xFF;
        final long byte233 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte229 & 127) << 32) | (byte230 << 24) | (byte231 << 16) | (byte232 << 8) | byte233;
        final long byte234 = blocks[blocksOffset++] & 0xFF;
        final long byte235 = blocks[blocksOffset++] & 0xFF;
        final long byte236 = blocks[blocksOffset++] & 0xFF;
        final long byte237 = blocks[blocksOffset++] & 0xFF;
        final long byte238 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = (byte234 << 31) | (byte235 << 23) | (byte236 << 15) | (byte237 << 7) | (byte238 >>> 1);
        final long byte239 = blocks[blocksOffset++] & 0xFF;
        final long byte240 = blocks[blocksOffset++] & 0xFF;
        final long byte241 = blocks[blocksOffset++] & 0xFF;
        final long byte242 = blocks[blocksOffset++] & 0xFF;
        final long byte243 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte238 & 1) << 38) | (byte239 << 30) | (byte240 << 22) | (byte241 << 14) | (byte242 << 6) | (byte243 >>> 2);
        final long byte244 = blocks[blocksOffset++] & 0xFF;
        final long byte245 = blocks[blocksOffset++] & 0xFF;
        final long byte246 = blocks[blocksOffset++] & 0xFF;
        final long byte247 = blocks[blocksOffset++] & 0xFF;
        final long byte248 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte243 & 3) << 37) | (byte244 << 29) | (byte245 << 21) | (byte246 << 13) | (byte247 << 5) | (byte248 >>> 3);
        final long byte249 = blocks[blocksOffset++] & 0xFF;
        final long byte250 = blocks[blocksOffset++] & 0xFF;
        final long byte251 = blocks[blocksOffset++] & 0xFF;
        final long byte252 = blocks[blocksOffset++] & 0xFF;
        final long byte253 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte248 & 7) << 36) | (byte249 << 28) | (byte250 << 20) | (byte251 << 12) | (byte252 << 4) | (byte253 >>> 4);
        final long byte254 = blocks[blocksOffset++] & 0xFF;
        final long byte255 = blocks[blocksOffset++] & 0xFF;
        final long byte256 = blocks[blocksOffset++] & 0xFF;
        final long byte257 = blocks[blocksOffset++] & 0xFF;
        final long byte258 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte253 & 15) << 35) | (byte254 << 27) | (byte255 << 19) | (byte256 << 11) | (byte257 << 3) | (byte258 >>> 5);
        final long byte259 = blocks[blocksOffset++] & 0xFF;
        final long byte260 = blocks[blocksOffset++] & 0xFF;
        final long byte261 = blocks[blocksOffset++] & 0xFF;
        final long byte262 = blocks[blocksOffset++] & 0xFF;
        final long byte263 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte258 & 31) << 34) | (byte259 << 26) | (byte260 << 18) | (byte261 << 10) | (byte262 << 2) | (byte263 >>> 6);
        final long byte264 = blocks[blocksOffset++] & 0xFF;
        final long byte265 = blocks[blocksOffset++] & 0xFF;
        final long byte266 = blocks[blocksOffset++] & 0xFF;
        final long byte267 = blocks[blocksOffset++] & 0xFF;
        final long byte268 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte263 & 63) << 33) | (byte264 << 25) | (byte265 << 17) | (byte266 << 9) | (byte267 << 1) | (byte268 >>> 7);
        final long byte269 = blocks[blocksOffset++] & 0xFF;
        final long byte270 = blocks[blocksOffset++] & 0xFF;
        final long byte271 = blocks[blocksOffset++] & 0xFF;
        final long byte272 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte268 & 127) << 32) | (byte269 << 24) | (byte270 << 16) | (byte271 << 8) | byte272;
        final long byte273 = blocks[blocksOffset++] & 0xFF;
        final long byte274 = blocks[blocksOffset++] & 0xFF;
        final long byte275 = blocks[blocksOffset++] & 0xFF;
        final long byte276 = blocks[blocksOffset++] & 0xFF;
        final long byte277 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = (byte273 << 31) | (byte274 << 23) | (byte275 << 15) | (byte276 << 7) | (byte277 >>> 1);
        final long byte278 = blocks[blocksOffset++] & 0xFF;
        final long byte279 = blocks[blocksOffset++] & 0xFF;
        final long byte280 = blocks[blocksOffset++] & 0xFF;
        final long byte281 = blocks[blocksOffset++] & 0xFF;
        final long byte282 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte277 & 1) << 38) | (byte278 << 30) | (byte279 << 22) | (byte280 << 14) | (byte281 << 6) | (byte282 >>> 2);
        final long byte283 = blocks[blocksOffset++] & 0xFF;
        final long byte284 = blocks[blocksOffset++] & 0xFF;
        final long byte285 = blocks[blocksOffset++] & 0xFF;
        final long byte286 = blocks[blocksOffset++] & 0xFF;
        final long byte287 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte282 & 3) << 37) | (byte283 << 29) | (byte284 << 21) | (byte285 << 13) | (byte286 << 5) | (byte287 >>> 3);
        final long byte288 = blocks[blocksOffset++] & 0xFF;
        final long byte289 = blocks[blocksOffset++] & 0xFF;
        final long byte290 = blocks[blocksOffset++] & 0xFF;
        final long byte291 = blocks[blocksOffset++] & 0xFF;
        final long byte292 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte287 & 7) << 36) | (byte288 << 28) | (byte289 << 20) | (byte290 << 12) | (byte291 << 4) | (byte292 >>> 4);
        final long byte293 = blocks[blocksOffset++] & 0xFF;
        final long byte294 = blocks[blocksOffset++] & 0xFF;
        final long byte295 = blocks[blocksOffset++] & 0xFF;
        final long byte296 = blocks[blocksOffset++] & 0xFF;
        final long byte297 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte292 & 15) << 35) | (byte293 << 27) | (byte294 << 19) | (byte295 << 11) | (byte296 << 3) | (byte297 >>> 5);
        final long byte298 = blocks[blocksOffset++] & 0xFF;
        final long byte299 = blocks[blocksOffset++] & 0xFF;
        final long byte300 = blocks[blocksOffset++] & 0xFF;
        final long byte301 = blocks[blocksOffset++] & 0xFF;
        final long byte302 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte297 & 31) << 34) | (byte298 << 26) | (byte299 << 18) | (byte300 << 10) | (byte301 << 2) | (byte302 >>> 6);
        final long byte303 = blocks[blocksOffset++] & 0xFF;
        final long byte304 = blocks[blocksOffset++] & 0xFF;
        final long byte305 = blocks[blocksOffset++] & 0xFF;
        final long byte306 = blocks[blocksOffset++] & 0xFF;
        final long byte307 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte302 & 63) << 33) | (byte303 << 25) | (byte304 << 17) | (byte305 << 9) | (byte306 << 1) | (byte307 >>> 7);
        final long byte308 = blocks[blocksOffset++] & 0xFF;
        final long byte309 = blocks[blocksOffset++] & 0xFF;
        final long byte310 = blocks[blocksOffset++] & 0xFF;
        final long byte311 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte307 & 127) << 32) | (byte308 << 24) | (byte309 << 16) | (byte310 << 8) | byte311;
      }
    }

    @Override
    public void encode(int[] values, int valuesOffset, long[] blocks, int blocksOffset, int iterations) {
      assert blocksOffset + iterations * blockCount() <= blocks.length;
      assert valuesOffset + iterations * valueCount() <= values.length;
      for (int i = 0; i < iterations; ++i) {
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 25) | ((values[valuesOffset] & 0xffffffffL) >>> 14);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 50) | ((values[valuesOffset++] & 0xffffffffL) << 11) | ((values[valuesOffset] & 0xffffffffL) >>> 28);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 36) | ((values[valuesOffset] & 0xffffffffL) >>> 3);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 61) | ((values[valuesOffset++] & 0xffffffffL) << 22) | ((values[valuesOffset] & 0xffffffffL) >>> 17);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 47) | ((values[valuesOffset++] & 0xffffffffL) << 8) | ((values[valuesOffset] & 0xffffffffL) >>> 31);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 33) | ((values[valuesOffset] & 0xffffffffL) >>> 6);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 58) | ((values[valuesOffset++] & 0xffffffffL) << 19) | ((values[valuesOffset] & 0xffffffffL) >>> 20);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 44) | ((values[valuesOffset++] & 0xffffffffL) << 5) | ((values[valuesOffset] & 0xffffffffL) >>> 34);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 30) | ((values[valuesOffset] & 0xffffffffL) >>> 9);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 55) | ((values[valuesOffset++] & 0xffffffffL) << 16) | ((values[valuesOffset] & 0xffffffffL) >>> 23);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 41) | ((values[valuesOffset++] & 0xffffffffL) << 2) | ((values[valuesOffset] & 0xffffffffL) >>> 37);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 27) | ((values[valuesOffset] & 0xffffffffL) >>> 12);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 52) | ((values[valuesOffset++] & 0xffffffffL) << 13) | ((values[valuesOffset] & 0xffffffffL) >>> 26);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 38) | ((values[valuesOffset] & 0xffffffffL) >>> 1);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 63) | ((values[valuesOffset++] & 0xffffffffL) << 24) | ((values[valuesOffset] & 0xffffffffL) >>> 15);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 49) | ((values[valuesOffset++] & 0xffffffffL) << 10) | ((values[valuesOffset] & 0xffffffffL) >>> 29);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 35) | ((values[valuesOffset] & 0xffffffffL) >>> 4);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 60) | ((values[valuesOffset++] & 0xffffffffL) << 21) | ((values[valuesOffset] & 0xffffffffL) >>> 18);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 46) | ((values[valuesOffset++] & 0xffffffffL) << 7) | ((values[valuesOffset] & 0xffffffffL) >>> 32);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 32) | ((values[valuesOffset] & 0xffffffffL) >>> 7);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 57) | ((values[valuesOffset++] & 0xffffffffL) << 18) | ((values[valuesOffset] & 0xffffffffL) >>> 21);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 43) | ((values[valuesOffset++] & 0xffffffffL) << 4) | ((values[valuesOffset] & 0xffffffffL) >>> 35);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 29) | ((values[valuesOffset] & 0xffffffffL) >>> 10);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 54) | ((values[valuesOffset++] & 0xffffffffL) << 15) | ((values[valuesOffset] & 0xffffffffL) >>> 24);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 40) | ((values[valuesOffset++] & 0xffffffffL) << 1) | ((values[valuesOffset] & 0xffffffffL) >>> 38);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 26) | ((values[valuesOffset] & 0xffffffffL) >>> 13);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 51) | ((values[valuesOffset++] & 0xffffffffL) << 12) | ((values[valuesOffset] & 0xffffffffL) >>> 27);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 37) | ((values[valuesOffset] & 0xffffffffL) >>> 2);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 62) | ((values[valuesOffset++] & 0xffffffffL) << 23) | ((values[valuesOffset] & 0xffffffffL) >>> 16);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 48) | ((values[valuesOffset++] & 0xffffffffL) << 9) | ((values[valuesOffset] & 0xffffffffL) >>> 30);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 34) | ((values[valuesOffset] & 0xffffffffL) >>> 5);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 59) | ((values[valuesOffset++] & 0xffffffffL) << 20) | ((values[valuesOffset] & 0xffffffffL) >>> 19);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 45) | ((values[valuesOffset++] & 0xffffffffL) << 6) | ((values[valuesOffset] & 0xffffffffL) >>> 33);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 31) | ((values[valuesOffset] & 0xffffffffL) >>> 8);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 56) | ((values[valuesOffset++] & 0xffffffffL) << 17) | ((values[valuesOffset] & 0xffffffffL) >>> 22);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 42) | ((values[valuesOffset++] & 0xffffffffL) << 3) | ((values[valuesOffset] & 0xffffffffL) >>> 36);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 28) | ((values[valuesOffset] & 0xffffffffL) >>> 11);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 53) | ((values[valuesOffset++] & 0xffffffffL) << 14) | ((values[valuesOffset] & 0xffffffffL) >>> 25);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 39) | (values[valuesOffset++] & 0xffffffffL);
      }
    }

    @Override
    public void encode(long[] values, int valuesOffset, long[] blocks, int blocksOffset, int iterations) {
      assert blocksOffset + iterations * blockCount() <= blocks.length;
      assert valuesOffset + iterations * valueCount() <= values.length;
      for (int i = 0; i < iterations; ++i) {
        blocks[blocksOffset++] = (values[valuesOffset++] << 25) | (values[valuesOffset] >>> 14);
        blocks[blocksOffset++] = (values[valuesOffset++] << 50) | (values[valuesOffset++] << 11) | (values[valuesOffset] >>> 28);
        blocks[blocksOffset++] = (values[valuesOffset++] << 36) | (values[valuesOffset] >>> 3);
        blocks[blocksOffset++] = (values[valuesOffset++] << 61) | (values[valuesOffset++] << 22) | (values[valuesOffset] >>> 17);
        blocks[blocksOffset++] = (values[valuesOffset++] << 47) | (values[valuesOffset++] << 8) | (values[valuesOffset] >>> 31);
        blocks[blocksOffset++] = (values[valuesOffset++] << 33) | (values[valuesOffset] >>> 6);
        blocks[blocksOffset++] = (values[valuesOffset++] << 58) | (values[valuesOffset++] << 19) | (values[valuesOffset] >>> 20);
        blocks[blocksOffset++] = (values[valuesOffset++] << 44) | (values[valuesOffset++] << 5) | (values[valuesOffset] >>> 34);
        blocks[blocksOffset++] = (values[valuesOffset++] << 30) | (values[valuesOffset] >>> 9);
        blocks[blocksOffset++] = (values[valuesOffset++] << 55) | (values[valuesOffset++] << 16) | (values[valuesOffset] >>> 23);
        blocks[blocksOffset++] = (values[valuesOffset++] << 41) | (values[valuesOffset++] << 2) | (values[valuesOffset] >>> 37);
        blocks[blocksOffset++] = (values[valuesOffset++] << 27) | (values[valuesOffset] >>> 12);
        blocks[blocksOffset++] = (values[valuesOffset++] << 52) | (values[valuesOffset++] << 13) | (values[valuesOffset] >>> 26);
        blocks[blocksOffset++] = (values[valuesOffset++] << 38) | (values[valuesOffset] >>> 1);
        blocks[blocksOffset++] = (values[valuesOffset++] << 63) | (values[valuesOffset++] << 24) | (values[valuesOffset] >>> 15);
        blocks[blocksOffset++] = (values[valuesOffset++] << 49) | (values[valuesOffset++] << 10) | (values[valuesOffset] >>> 29);
        blocks[blocksOffset++] = (values[valuesOffset++] << 35) | (values[valuesOffset] >>> 4);
        blocks[blocksOffset++] = (values[valuesOffset++] << 60) | (values[valuesOffset++] << 21) | (values[valuesOffset] >>> 18);
        blocks[blocksOffset++] = (values[valuesOffset++] << 46) | (values[valuesOffset++] << 7) | (values[valuesOffset] >>> 32);
        blocks[blocksOffset++] = (values[valuesOffset++] << 32) | (values[valuesOffset] >>> 7);
        blocks[blocksOffset++] = (values[valuesOffset++] << 57) | (values[valuesOffset++] << 18) | (values[valuesOffset] >>> 21);
        blocks[blocksOffset++] = (values[valuesOffset++] << 43) | (values[valuesOffset++] << 4) | (values[valuesOffset] >>> 35);
        blocks[blocksOffset++] = (values[valuesOffset++] << 29) | (values[valuesOffset] >>> 10);
        blocks[blocksOffset++] = (values[valuesOffset++] << 54) | (values[valuesOffset++] << 15) | (values[valuesOffset] >>> 24);
        blocks[blocksOffset++] = (values[valuesOffset++] << 40) | (values[valuesOffset++] << 1) | (values[valuesOffset] >>> 38);
        blocks[blocksOffset++] = (values[valuesOffset++] << 26) | (values[valuesOffset] >>> 13);
        blocks[blocksOffset++] = (values[valuesOffset++] << 51) | (values[valuesOffset++] << 12) | (values[valuesOffset] >>> 27);
        blocks[blocksOffset++] = (values[valuesOffset++] << 37) | (values[valuesOffset] >>> 2);
        blocks[blocksOffset++] = (values[valuesOffset++] << 62) | (values[valuesOffset++] << 23) | (values[valuesOffset] >>> 16);
        blocks[blocksOffset++] = (values[valuesOffset++] << 48) | (values[valuesOffset++] << 9) | (values[valuesOffset] >>> 30);
        blocks[blocksOffset++] = (values[valuesOffset++] << 34) | (values[valuesOffset] >>> 5);
        blocks[blocksOffset++] = (values[valuesOffset++] << 59) | (values[valuesOffset++] << 20) | (values[valuesOffset] >>> 19);
        blocks[blocksOffset++] = (values[valuesOffset++] << 45) | (values[valuesOffset++] << 6) | (values[valuesOffset] >>> 33);
        blocks[blocksOffset++] = (values[valuesOffset++] << 31) | (values[valuesOffset] >>> 8);
        blocks[blocksOffset++] = (values[valuesOffset++] << 56) | (values[valuesOffset++] << 17) | (values[valuesOffset] >>> 22);
        blocks[blocksOffset++] = (values[valuesOffset++] << 42) | (values[valuesOffset++] << 3) | (values[valuesOffset] >>> 36);
        blocks[blocksOffset++] = (values[valuesOffset++] << 28) | (values[valuesOffset] >>> 11);
        blocks[blocksOffset++] = (values[valuesOffset++] << 53) | (values[valuesOffset++] << 14) | (values[valuesOffset] >>> 25);
        blocks[blocksOffset++] = (values[valuesOffset++] << 39) | values[valuesOffset++];
      }
    }

}
