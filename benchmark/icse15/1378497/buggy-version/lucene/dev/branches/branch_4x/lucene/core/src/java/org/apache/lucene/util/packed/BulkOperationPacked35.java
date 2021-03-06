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
final class BulkOperationPacked35 extends BulkOperation {
    @Override
    public int blockCount() {
      return 35;
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
        values[valuesOffset++] = block0 >>> 29;
        final long block1 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block0 & 536870911L) << 6) | (block1 >>> 58);
        values[valuesOffset++] = (block1 >>> 23) & 34359738367L;
        final long block2 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block1 & 8388607L) << 12) | (block2 >>> 52);
        values[valuesOffset++] = (block2 >>> 17) & 34359738367L;
        final long block3 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block2 & 131071L) << 18) | (block3 >>> 46);
        values[valuesOffset++] = (block3 >>> 11) & 34359738367L;
        final long block4 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block3 & 2047L) << 24) | (block4 >>> 40);
        values[valuesOffset++] = (block4 >>> 5) & 34359738367L;
        final long block5 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block4 & 31L) << 30) | (block5 >>> 34);
        final long block6 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block5 & 17179869183L) << 1) | (block6 >>> 63);
        values[valuesOffset++] = (block6 >>> 28) & 34359738367L;
        final long block7 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block6 & 268435455L) << 7) | (block7 >>> 57);
        values[valuesOffset++] = (block7 >>> 22) & 34359738367L;
        final long block8 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block7 & 4194303L) << 13) | (block8 >>> 51);
        values[valuesOffset++] = (block8 >>> 16) & 34359738367L;
        final long block9 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block8 & 65535L) << 19) | (block9 >>> 45);
        values[valuesOffset++] = (block9 >>> 10) & 34359738367L;
        final long block10 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block9 & 1023L) << 25) | (block10 >>> 39);
        values[valuesOffset++] = (block10 >>> 4) & 34359738367L;
        final long block11 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block10 & 15L) << 31) | (block11 >>> 33);
        final long block12 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block11 & 8589934591L) << 2) | (block12 >>> 62);
        values[valuesOffset++] = (block12 >>> 27) & 34359738367L;
        final long block13 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block12 & 134217727L) << 8) | (block13 >>> 56);
        values[valuesOffset++] = (block13 >>> 21) & 34359738367L;
        final long block14 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block13 & 2097151L) << 14) | (block14 >>> 50);
        values[valuesOffset++] = (block14 >>> 15) & 34359738367L;
        final long block15 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block14 & 32767L) << 20) | (block15 >>> 44);
        values[valuesOffset++] = (block15 >>> 9) & 34359738367L;
        final long block16 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block15 & 511L) << 26) | (block16 >>> 38);
        values[valuesOffset++] = (block16 >>> 3) & 34359738367L;
        final long block17 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block16 & 7L) << 32) | (block17 >>> 32);
        final long block18 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block17 & 4294967295L) << 3) | (block18 >>> 61);
        values[valuesOffset++] = (block18 >>> 26) & 34359738367L;
        final long block19 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block18 & 67108863L) << 9) | (block19 >>> 55);
        values[valuesOffset++] = (block19 >>> 20) & 34359738367L;
        final long block20 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block19 & 1048575L) << 15) | (block20 >>> 49);
        values[valuesOffset++] = (block20 >>> 14) & 34359738367L;
        final long block21 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block20 & 16383L) << 21) | (block21 >>> 43);
        values[valuesOffset++] = (block21 >>> 8) & 34359738367L;
        final long block22 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block21 & 255L) << 27) | (block22 >>> 37);
        values[valuesOffset++] = (block22 >>> 2) & 34359738367L;
        final long block23 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block22 & 3L) << 33) | (block23 >>> 31);
        final long block24 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block23 & 2147483647L) << 4) | (block24 >>> 60);
        values[valuesOffset++] = (block24 >>> 25) & 34359738367L;
        final long block25 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block24 & 33554431L) << 10) | (block25 >>> 54);
        values[valuesOffset++] = (block25 >>> 19) & 34359738367L;
        final long block26 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block25 & 524287L) << 16) | (block26 >>> 48);
        values[valuesOffset++] = (block26 >>> 13) & 34359738367L;
        final long block27 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block26 & 8191L) << 22) | (block27 >>> 42);
        values[valuesOffset++] = (block27 >>> 7) & 34359738367L;
        final long block28 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block27 & 127L) << 28) | (block28 >>> 36);
        values[valuesOffset++] = (block28 >>> 1) & 34359738367L;
        final long block29 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block28 & 1L) << 34) | (block29 >>> 30);
        final long block30 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block29 & 1073741823L) << 5) | (block30 >>> 59);
        values[valuesOffset++] = (block30 >>> 24) & 34359738367L;
        final long block31 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block30 & 16777215L) << 11) | (block31 >>> 53);
        values[valuesOffset++] = (block31 >>> 18) & 34359738367L;
        final long block32 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block31 & 262143L) << 17) | (block32 >>> 47);
        values[valuesOffset++] = (block32 >>> 12) & 34359738367L;
        final long block33 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block32 & 4095L) << 23) | (block33 >>> 41);
        values[valuesOffset++] = (block33 >>> 6) & 34359738367L;
        final long block34 = blocks[blocksOffset++];
        values[valuesOffset++] = ((block33 & 63L) << 29) | (block34 >>> 35);
        values[valuesOffset++] = block34 & 34359738367L;
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
        values[valuesOffset++] = (byte0 << 27) | (byte1 << 19) | (byte2 << 11) | (byte3 << 3) | (byte4 >>> 5);
        final long byte5 = blocks[blocksOffset++] & 0xFF;
        final long byte6 = blocks[blocksOffset++] & 0xFF;
        final long byte7 = blocks[blocksOffset++] & 0xFF;
        final long byte8 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte4 & 31) << 30) | (byte5 << 22) | (byte6 << 14) | (byte7 << 6) | (byte8 >>> 2);
        final long byte9 = blocks[blocksOffset++] & 0xFF;
        final long byte10 = blocks[blocksOffset++] & 0xFF;
        final long byte11 = blocks[blocksOffset++] & 0xFF;
        final long byte12 = blocks[blocksOffset++] & 0xFF;
        final long byte13 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte8 & 3) << 33) | (byte9 << 25) | (byte10 << 17) | (byte11 << 9) | (byte12 << 1) | (byte13 >>> 7);
        final long byte14 = blocks[blocksOffset++] & 0xFF;
        final long byte15 = blocks[blocksOffset++] & 0xFF;
        final long byte16 = blocks[blocksOffset++] & 0xFF;
        final long byte17 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte13 & 127) << 28) | (byte14 << 20) | (byte15 << 12) | (byte16 << 4) | (byte17 >>> 4);
        final long byte18 = blocks[blocksOffset++] & 0xFF;
        final long byte19 = blocks[blocksOffset++] & 0xFF;
        final long byte20 = blocks[blocksOffset++] & 0xFF;
        final long byte21 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte17 & 15) << 31) | (byte18 << 23) | (byte19 << 15) | (byte20 << 7) | (byte21 >>> 1);
        final long byte22 = blocks[blocksOffset++] & 0xFF;
        final long byte23 = blocks[blocksOffset++] & 0xFF;
        final long byte24 = blocks[blocksOffset++] & 0xFF;
        final long byte25 = blocks[blocksOffset++] & 0xFF;
        final long byte26 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte21 & 1) << 34) | (byte22 << 26) | (byte23 << 18) | (byte24 << 10) | (byte25 << 2) | (byte26 >>> 6);
        final long byte27 = blocks[blocksOffset++] & 0xFF;
        final long byte28 = blocks[blocksOffset++] & 0xFF;
        final long byte29 = blocks[blocksOffset++] & 0xFF;
        final long byte30 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte26 & 63) << 29) | (byte27 << 21) | (byte28 << 13) | (byte29 << 5) | (byte30 >>> 3);
        final long byte31 = blocks[blocksOffset++] & 0xFF;
        final long byte32 = blocks[blocksOffset++] & 0xFF;
        final long byte33 = blocks[blocksOffset++] & 0xFF;
        final long byte34 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte30 & 7) << 32) | (byte31 << 24) | (byte32 << 16) | (byte33 << 8) | byte34;
        final long byte35 = blocks[blocksOffset++] & 0xFF;
        final long byte36 = blocks[blocksOffset++] & 0xFF;
        final long byte37 = blocks[blocksOffset++] & 0xFF;
        final long byte38 = blocks[blocksOffset++] & 0xFF;
        final long byte39 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = (byte35 << 27) | (byte36 << 19) | (byte37 << 11) | (byte38 << 3) | (byte39 >>> 5);
        final long byte40 = blocks[blocksOffset++] & 0xFF;
        final long byte41 = blocks[blocksOffset++] & 0xFF;
        final long byte42 = blocks[blocksOffset++] & 0xFF;
        final long byte43 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte39 & 31) << 30) | (byte40 << 22) | (byte41 << 14) | (byte42 << 6) | (byte43 >>> 2);
        final long byte44 = blocks[blocksOffset++] & 0xFF;
        final long byte45 = blocks[blocksOffset++] & 0xFF;
        final long byte46 = blocks[blocksOffset++] & 0xFF;
        final long byte47 = blocks[blocksOffset++] & 0xFF;
        final long byte48 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte43 & 3) << 33) | (byte44 << 25) | (byte45 << 17) | (byte46 << 9) | (byte47 << 1) | (byte48 >>> 7);
        final long byte49 = blocks[blocksOffset++] & 0xFF;
        final long byte50 = blocks[blocksOffset++] & 0xFF;
        final long byte51 = blocks[blocksOffset++] & 0xFF;
        final long byte52 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte48 & 127) << 28) | (byte49 << 20) | (byte50 << 12) | (byte51 << 4) | (byte52 >>> 4);
        final long byte53 = blocks[blocksOffset++] & 0xFF;
        final long byte54 = blocks[blocksOffset++] & 0xFF;
        final long byte55 = blocks[blocksOffset++] & 0xFF;
        final long byte56 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte52 & 15) << 31) | (byte53 << 23) | (byte54 << 15) | (byte55 << 7) | (byte56 >>> 1);
        final long byte57 = blocks[blocksOffset++] & 0xFF;
        final long byte58 = blocks[blocksOffset++] & 0xFF;
        final long byte59 = blocks[blocksOffset++] & 0xFF;
        final long byte60 = blocks[blocksOffset++] & 0xFF;
        final long byte61 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte56 & 1) << 34) | (byte57 << 26) | (byte58 << 18) | (byte59 << 10) | (byte60 << 2) | (byte61 >>> 6);
        final long byte62 = blocks[blocksOffset++] & 0xFF;
        final long byte63 = blocks[blocksOffset++] & 0xFF;
        final long byte64 = blocks[blocksOffset++] & 0xFF;
        final long byte65 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte61 & 63) << 29) | (byte62 << 21) | (byte63 << 13) | (byte64 << 5) | (byte65 >>> 3);
        final long byte66 = blocks[blocksOffset++] & 0xFF;
        final long byte67 = blocks[blocksOffset++] & 0xFF;
        final long byte68 = blocks[blocksOffset++] & 0xFF;
        final long byte69 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte65 & 7) << 32) | (byte66 << 24) | (byte67 << 16) | (byte68 << 8) | byte69;
        final long byte70 = blocks[blocksOffset++] & 0xFF;
        final long byte71 = blocks[blocksOffset++] & 0xFF;
        final long byte72 = blocks[blocksOffset++] & 0xFF;
        final long byte73 = blocks[blocksOffset++] & 0xFF;
        final long byte74 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = (byte70 << 27) | (byte71 << 19) | (byte72 << 11) | (byte73 << 3) | (byte74 >>> 5);
        final long byte75 = blocks[blocksOffset++] & 0xFF;
        final long byte76 = blocks[blocksOffset++] & 0xFF;
        final long byte77 = blocks[blocksOffset++] & 0xFF;
        final long byte78 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte74 & 31) << 30) | (byte75 << 22) | (byte76 << 14) | (byte77 << 6) | (byte78 >>> 2);
        final long byte79 = blocks[blocksOffset++] & 0xFF;
        final long byte80 = blocks[blocksOffset++] & 0xFF;
        final long byte81 = blocks[blocksOffset++] & 0xFF;
        final long byte82 = blocks[blocksOffset++] & 0xFF;
        final long byte83 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte78 & 3) << 33) | (byte79 << 25) | (byte80 << 17) | (byte81 << 9) | (byte82 << 1) | (byte83 >>> 7);
        final long byte84 = blocks[blocksOffset++] & 0xFF;
        final long byte85 = blocks[blocksOffset++] & 0xFF;
        final long byte86 = blocks[blocksOffset++] & 0xFF;
        final long byte87 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte83 & 127) << 28) | (byte84 << 20) | (byte85 << 12) | (byte86 << 4) | (byte87 >>> 4);
        final long byte88 = blocks[blocksOffset++] & 0xFF;
        final long byte89 = blocks[blocksOffset++] & 0xFF;
        final long byte90 = blocks[blocksOffset++] & 0xFF;
        final long byte91 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte87 & 15) << 31) | (byte88 << 23) | (byte89 << 15) | (byte90 << 7) | (byte91 >>> 1);
        final long byte92 = blocks[blocksOffset++] & 0xFF;
        final long byte93 = blocks[blocksOffset++] & 0xFF;
        final long byte94 = blocks[blocksOffset++] & 0xFF;
        final long byte95 = blocks[blocksOffset++] & 0xFF;
        final long byte96 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte91 & 1) << 34) | (byte92 << 26) | (byte93 << 18) | (byte94 << 10) | (byte95 << 2) | (byte96 >>> 6);
        final long byte97 = blocks[blocksOffset++] & 0xFF;
        final long byte98 = blocks[blocksOffset++] & 0xFF;
        final long byte99 = blocks[blocksOffset++] & 0xFF;
        final long byte100 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte96 & 63) << 29) | (byte97 << 21) | (byte98 << 13) | (byte99 << 5) | (byte100 >>> 3);
        final long byte101 = blocks[blocksOffset++] & 0xFF;
        final long byte102 = blocks[blocksOffset++] & 0xFF;
        final long byte103 = blocks[blocksOffset++] & 0xFF;
        final long byte104 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte100 & 7) << 32) | (byte101 << 24) | (byte102 << 16) | (byte103 << 8) | byte104;
        final long byte105 = blocks[blocksOffset++] & 0xFF;
        final long byte106 = blocks[blocksOffset++] & 0xFF;
        final long byte107 = blocks[blocksOffset++] & 0xFF;
        final long byte108 = blocks[blocksOffset++] & 0xFF;
        final long byte109 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = (byte105 << 27) | (byte106 << 19) | (byte107 << 11) | (byte108 << 3) | (byte109 >>> 5);
        final long byte110 = blocks[blocksOffset++] & 0xFF;
        final long byte111 = blocks[blocksOffset++] & 0xFF;
        final long byte112 = blocks[blocksOffset++] & 0xFF;
        final long byte113 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte109 & 31) << 30) | (byte110 << 22) | (byte111 << 14) | (byte112 << 6) | (byte113 >>> 2);
        final long byte114 = blocks[blocksOffset++] & 0xFF;
        final long byte115 = blocks[blocksOffset++] & 0xFF;
        final long byte116 = blocks[blocksOffset++] & 0xFF;
        final long byte117 = blocks[blocksOffset++] & 0xFF;
        final long byte118 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte113 & 3) << 33) | (byte114 << 25) | (byte115 << 17) | (byte116 << 9) | (byte117 << 1) | (byte118 >>> 7);
        final long byte119 = blocks[blocksOffset++] & 0xFF;
        final long byte120 = blocks[blocksOffset++] & 0xFF;
        final long byte121 = blocks[blocksOffset++] & 0xFF;
        final long byte122 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte118 & 127) << 28) | (byte119 << 20) | (byte120 << 12) | (byte121 << 4) | (byte122 >>> 4);
        final long byte123 = blocks[blocksOffset++] & 0xFF;
        final long byte124 = blocks[blocksOffset++] & 0xFF;
        final long byte125 = blocks[blocksOffset++] & 0xFF;
        final long byte126 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte122 & 15) << 31) | (byte123 << 23) | (byte124 << 15) | (byte125 << 7) | (byte126 >>> 1);
        final long byte127 = blocks[blocksOffset++] & 0xFF;
        final long byte128 = blocks[blocksOffset++] & 0xFF;
        final long byte129 = blocks[blocksOffset++] & 0xFF;
        final long byte130 = blocks[blocksOffset++] & 0xFF;
        final long byte131 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte126 & 1) << 34) | (byte127 << 26) | (byte128 << 18) | (byte129 << 10) | (byte130 << 2) | (byte131 >>> 6);
        final long byte132 = blocks[blocksOffset++] & 0xFF;
        final long byte133 = blocks[blocksOffset++] & 0xFF;
        final long byte134 = blocks[blocksOffset++] & 0xFF;
        final long byte135 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte131 & 63) << 29) | (byte132 << 21) | (byte133 << 13) | (byte134 << 5) | (byte135 >>> 3);
        final long byte136 = blocks[blocksOffset++] & 0xFF;
        final long byte137 = blocks[blocksOffset++] & 0xFF;
        final long byte138 = blocks[blocksOffset++] & 0xFF;
        final long byte139 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte135 & 7) << 32) | (byte136 << 24) | (byte137 << 16) | (byte138 << 8) | byte139;
        final long byte140 = blocks[blocksOffset++] & 0xFF;
        final long byte141 = blocks[blocksOffset++] & 0xFF;
        final long byte142 = blocks[blocksOffset++] & 0xFF;
        final long byte143 = blocks[blocksOffset++] & 0xFF;
        final long byte144 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = (byte140 << 27) | (byte141 << 19) | (byte142 << 11) | (byte143 << 3) | (byte144 >>> 5);
        final long byte145 = blocks[blocksOffset++] & 0xFF;
        final long byte146 = blocks[blocksOffset++] & 0xFF;
        final long byte147 = blocks[blocksOffset++] & 0xFF;
        final long byte148 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte144 & 31) << 30) | (byte145 << 22) | (byte146 << 14) | (byte147 << 6) | (byte148 >>> 2);
        final long byte149 = blocks[blocksOffset++] & 0xFF;
        final long byte150 = blocks[blocksOffset++] & 0xFF;
        final long byte151 = blocks[blocksOffset++] & 0xFF;
        final long byte152 = blocks[blocksOffset++] & 0xFF;
        final long byte153 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte148 & 3) << 33) | (byte149 << 25) | (byte150 << 17) | (byte151 << 9) | (byte152 << 1) | (byte153 >>> 7);
        final long byte154 = blocks[blocksOffset++] & 0xFF;
        final long byte155 = blocks[blocksOffset++] & 0xFF;
        final long byte156 = blocks[blocksOffset++] & 0xFF;
        final long byte157 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte153 & 127) << 28) | (byte154 << 20) | (byte155 << 12) | (byte156 << 4) | (byte157 >>> 4);
        final long byte158 = blocks[blocksOffset++] & 0xFF;
        final long byte159 = blocks[blocksOffset++] & 0xFF;
        final long byte160 = blocks[blocksOffset++] & 0xFF;
        final long byte161 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte157 & 15) << 31) | (byte158 << 23) | (byte159 << 15) | (byte160 << 7) | (byte161 >>> 1);
        final long byte162 = blocks[blocksOffset++] & 0xFF;
        final long byte163 = blocks[blocksOffset++] & 0xFF;
        final long byte164 = blocks[blocksOffset++] & 0xFF;
        final long byte165 = blocks[blocksOffset++] & 0xFF;
        final long byte166 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte161 & 1) << 34) | (byte162 << 26) | (byte163 << 18) | (byte164 << 10) | (byte165 << 2) | (byte166 >>> 6);
        final long byte167 = blocks[blocksOffset++] & 0xFF;
        final long byte168 = blocks[blocksOffset++] & 0xFF;
        final long byte169 = blocks[blocksOffset++] & 0xFF;
        final long byte170 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte166 & 63) << 29) | (byte167 << 21) | (byte168 << 13) | (byte169 << 5) | (byte170 >>> 3);
        final long byte171 = blocks[blocksOffset++] & 0xFF;
        final long byte172 = blocks[blocksOffset++] & 0xFF;
        final long byte173 = blocks[blocksOffset++] & 0xFF;
        final long byte174 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte170 & 7) << 32) | (byte171 << 24) | (byte172 << 16) | (byte173 << 8) | byte174;
        final long byte175 = blocks[blocksOffset++] & 0xFF;
        final long byte176 = blocks[blocksOffset++] & 0xFF;
        final long byte177 = blocks[blocksOffset++] & 0xFF;
        final long byte178 = blocks[blocksOffset++] & 0xFF;
        final long byte179 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = (byte175 << 27) | (byte176 << 19) | (byte177 << 11) | (byte178 << 3) | (byte179 >>> 5);
        final long byte180 = blocks[blocksOffset++] & 0xFF;
        final long byte181 = blocks[blocksOffset++] & 0xFF;
        final long byte182 = blocks[blocksOffset++] & 0xFF;
        final long byte183 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte179 & 31) << 30) | (byte180 << 22) | (byte181 << 14) | (byte182 << 6) | (byte183 >>> 2);
        final long byte184 = blocks[blocksOffset++] & 0xFF;
        final long byte185 = blocks[blocksOffset++] & 0xFF;
        final long byte186 = blocks[blocksOffset++] & 0xFF;
        final long byte187 = blocks[blocksOffset++] & 0xFF;
        final long byte188 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte183 & 3) << 33) | (byte184 << 25) | (byte185 << 17) | (byte186 << 9) | (byte187 << 1) | (byte188 >>> 7);
        final long byte189 = blocks[blocksOffset++] & 0xFF;
        final long byte190 = blocks[blocksOffset++] & 0xFF;
        final long byte191 = blocks[blocksOffset++] & 0xFF;
        final long byte192 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte188 & 127) << 28) | (byte189 << 20) | (byte190 << 12) | (byte191 << 4) | (byte192 >>> 4);
        final long byte193 = blocks[blocksOffset++] & 0xFF;
        final long byte194 = blocks[blocksOffset++] & 0xFF;
        final long byte195 = blocks[blocksOffset++] & 0xFF;
        final long byte196 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte192 & 15) << 31) | (byte193 << 23) | (byte194 << 15) | (byte195 << 7) | (byte196 >>> 1);
        final long byte197 = blocks[blocksOffset++] & 0xFF;
        final long byte198 = blocks[blocksOffset++] & 0xFF;
        final long byte199 = blocks[blocksOffset++] & 0xFF;
        final long byte200 = blocks[blocksOffset++] & 0xFF;
        final long byte201 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte196 & 1) << 34) | (byte197 << 26) | (byte198 << 18) | (byte199 << 10) | (byte200 << 2) | (byte201 >>> 6);
        final long byte202 = blocks[blocksOffset++] & 0xFF;
        final long byte203 = blocks[blocksOffset++] & 0xFF;
        final long byte204 = blocks[blocksOffset++] & 0xFF;
        final long byte205 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte201 & 63) << 29) | (byte202 << 21) | (byte203 << 13) | (byte204 << 5) | (byte205 >>> 3);
        final long byte206 = blocks[blocksOffset++] & 0xFF;
        final long byte207 = blocks[blocksOffset++] & 0xFF;
        final long byte208 = blocks[blocksOffset++] & 0xFF;
        final long byte209 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte205 & 7) << 32) | (byte206 << 24) | (byte207 << 16) | (byte208 << 8) | byte209;
        final long byte210 = blocks[blocksOffset++] & 0xFF;
        final long byte211 = blocks[blocksOffset++] & 0xFF;
        final long byte212 = blocks[blocksOffset++] & 0xFF;
        final long byte213 = blocks[blocksOffset++] & 0xFF;
        final long byte214 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = (byte210 << 27) | (byte211 << 19) | (byte212 << 11) | (byte213 << 3) | (byte214 >>> 5);
        final long byte215 = blocks[blocksOffset++] & 0xFF;
        final long byte216 = blocks[blocksOffset++] & 0xFF;
        final long byte217 = blocks[blocksOffset++] & 0xFF;
        final long byte218 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte214 & 31) << 30) | (byte215 << 22) | (byte216 << 14) | (byte217 << 6) | (byte218 >>> 2);
        final long byte219 = blocks[blocksOffset++] & 0xFF;
        final long byte220 = blocks[blocksOffset++] & 0xFF;
        final long byte221 = blocks[blocksOffset++] & 0xFF;
        final long byte222 = blocks[blocksOffset++] & 0xFF;
        final long byte223 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte218 & 3) << 33) | (byte219 << 25) | (byte220 << 17) | (byte221 << 9) | (byte222 << 1) | (byte223 >>> 7);
        final long byte224 = blocks[blocksOffset++] & 0xFF;
        final long byte225 = blocks[blocksOffset++] & 0xFF;
        final long byte226 = blocks[blocksOffset++] & 0xFF;
        final long byte227 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte223 & 127) << 28) | (byte224 << 20) | (byte225 << 12) | (byte226 << 4) | (byte227 >>> 4);
        final long byte228 = blocks[blocksOffset++] & 0xFF;
        final long byte229 = blocks[blocksOffset++] & 0xFF;
        final long byte230 = blocks[blocksOffset++] & 0xFF;
        final long byte231 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte227 & 15) << 31) | (byte228 << 23) | (byte229 << 15) | (byte230 << 7) | (byte231 >>> 1);
        final long byte232 = blocks[blocksOffset++] & 0xFF;
        final long byte233 = blocks[blocksOffset++] & 0xFF;
        final long byte234 = blocks[blocksOffset++] & 0xFF;
        final long byte235 = blocks[blocksOffset++] & 0xFF;
        final long byte236 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte231 & 1) << 34) | (byte232 << 26) | (byte233 << 18) | (byte234 << 10) | (byte235 << 2) | (byte236 >>> 6);
        final long byte237 = blocks[blocksOffset++] & 0xFF;
        final long byte238 = blocks[blocksOffset++] & 0xFF;
        final long byte239 = blocks[blocksOffset++] & 0xFF;
        final long byte240 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte236 & 63) << 29) | (byte237 << 21) | (byte238 << 13) | (byte239 << 5) | (byte240 >>> 3);
        final long byte241 = blocks[blocksOffset++] & 0xFF;
        final long byte242 = blocks[blocksOffset++] & 0xFF;
        final long byte243 = blocks[blocksOffset++] & 0xFF;
        final long byte244 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte240 & 7) << 32) | (byte241 << 24) | (byte242 << 16) | (byte243 << 8) | byte244;
        final long byte245 = blocks[blocksOffset++] & 0xFF;
        final long byte246 = blocks[blocksOffset++] & 0xFF;
        final long byte247 = blocks[blocksOffset++] & 0xFF;
        final long byte248 = blocks[blocksOffset++] & 0xFF;
        final long byte249 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = (byte245 << 27) | (byte246 << 19) | (byte247 << 11) | (byte248 << 3) | (byte249 >>> 5);
        final long byte250 = blocks[blocksOffset++] & 0xFF;
        final long byte251 = blocks[blocksOffset++] & 0xFF;
        final long byte252 = blocks[blocksOffset++] & 0xFF;
        final long byte253 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte249 & 31) << 30) | (byte250 << 22) | (byte251 << 14) | (byte252 << 6) | (byte253 >>> 2);
        final long byte254 = blocks[blocksOffset++] & 0xFF;
        final long byte255 = blocks[blocksOffset++] & 0xFF;
        final long byte256 = blocks[blocksOffset++] & 0xFF;
        final long byte257 = blocks[blocksOffset++] & 0xFF;
        final long byte258 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte253 & 3) << 33) | (byte254 << 25) | (byte255 << 17) | (byte256 << 9) | (byte257 << 1) | (byte258 >>> 7);
        final long byte259 = blocks[blocksOffset++] & 0xFF;
        final long byte260 = blocks[blocksOffset++] & 0xFF;
        final long byte261 = blocks[blocksOffset++] & 0xFF;
        final long byte262 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte258 & 127) << 28) | (byte259 << 20) | (byte260 << 12) | (byte261 << 4) | (byte262 >>> 4);
        final long byte263 = blocks[blocksOffset++] & 0xFF;
        final long byte264 = blocks[blocksOffset++] & 0xFF;
        final long byte265 = blocks[blocksOffset++] & 0xFF;
        final long byte266 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte262 & 15) << 31) | (byte263 << 23) | (byte264 << 15) | (byte265 << 7) | (byte266 >>> 1);
        final long byte267 = blocks[blocksOffset++] & 0xFF;
        final long byte268 = blocks[blocksOffset++] & 0xFF;
        final long byte269 = blocks[blocksOffset++] & 0xFF;
        final long byte270 = blocks[blocksOffset++] & 0xFF;
        final long byte271 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte266 & 1) << 34) | (byte267 << 26) | (byte268 << 18) | (byte269 << 10) | (byte270 << 2) | (byte271 >>> 6);
        final long byte272 = blocks[blocksOffset++] & 0xFF;
        final long byte273 = blocks[blocksOffset++] & 0xFF;
        final long byte274 = blocks[blocksOffset++] & 0xFF;
        final long byte275 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte271 & 63) << 29) | (byte272 << 21) | (byte273 << 13) | (byte274 << 5) | (byte275 >>> 3);
        final long byte276 = blocks[blocksOffset++] & 0xFF;
        final long byte277 = blocks[blocksOffset++] & 0xFF;
        final long byte278 = blocks[blocksOffset++] & 0xFF;
        final long byte279 = blocks[blocksOffset++] & 0xFF;
        values[valuesOffset++] = ((byte275 & 7) << 32) | (byte276 << 24) | (byte277 << 16) | (byte278 << 8) | byte279;
      }
    }

    @Override
    public void encode(int[] values, int valuesOffset, long[] blocks, int blocksOffset, int iterations) {
      assert blocksOffset + iterations * blockCount() <= blocks.length;
      assert valuesOffset + iterations * valueCount() <= values.length;
      for (int i = 0; i < iterations; ++i) {
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 29) | ((values[valuesOffset] & 0xffffffffL) >>> 6);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 58) | ((values[valuesOffset++] & 0xffffffffL) << 23) | ((values[valuesOffset] & 0xffffffffL) >>> 12);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 52) | ((values[valuesOffset++] & 0xffffffffL) << 17) | ((values[valuesOffset] & 0xffffffffL) >>> 18);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 46) | ((values[valuesOffset++] & 0xffffffffL) << 11) | ((values[valuesOffset] & 0xffffffffL) >>> 24);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 40) | ((values[valuesOffset++] & 0xffffffffL) << 5) | ((values[valuesOffset] & 0xffffffffL) >>> 30);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 34) | ((values[valuesOffset] & 0xffffffffL) >>> 1);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 63) | ((values[valuesOffset++] & 0xffffffffL) << 28) | ((values[valuesOffset] & 0xffffffffL) >>> 7);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 57) | ((values[valuesOffset++] & 0xffffffffL) << 22) | ((values[valuesOffset] & 0xffffffffL) >>> 13);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 51) | ((values[valuesOffset++] & 0xffffffffL) << 16) | ((values[valuesOffset] & 0xffffffffL) >>> 19);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 45) | ((values[valuesOffset++] & 0xffffffffL) << 10) | ((values[valuesOffset] & 0xffffffffL) >>> 25);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 39) | ((values[valuesOffset++] & 0xffffffffL) << 4) | ((values[valuesOffset] & 0xffffffffL) >>> 31);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 33) | ((values[valuesOffset] & 0xffffffffL) >>> 2);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 62) | ((values[valuesOffset++] & 0xffffffffL) << 27) | ((values[valuesOffset] & 0xffffffffL) >>> 8);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 56) | ((values[valuesOffset++] & 0xffffffffL) << 21) | ((values[valuesOffset] & 0xffffffffL) >>> 14);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 50) | ((values[valuesOffset++] & 0xffffffffL) << 15) | ((values[valuesOffset] & 0xffffffffL) >>> 20);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 44) | ((values[valuesOffset++] & 0xffffffffL) << 9) | ((values[valuesOffset] & 0xffffffffL) >>> 26);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 38) | ((values[valuesOffset++] & 0xffffffffL) << 3) | ((values[valuesOffset] & 0xffffffffL) >>> 32);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 32) | ((values[valuesOffset] & 0xffffffffL) >>> 3);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 61) | ((values[valuesOffset++] & 0xffffffffL) << 26) | ((values[valuesOffset] & 0xffffffffL) >>> 9);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 55) | ((values[valuesOffset++] & 0xffffffffL) << 20) | ((values[valuesOffset] & 0xffffffffL) >>> 15);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 49) | ((values[valuesOffset++] & 0xffffffffL) << 14) | ((values[valuesOffset] & 0xffffffffL) >>> 21);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 43) | ((values[valuesOffset++] & 0xffffffffL) << 8) | ((values[valuesOffset] & 0xffffffffL) >>> 27);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 37) | ((values[valuesOffset++] & 0xffffffffL) << 2) | ((values[valuesOffset] & 0xffffffffL) >>> 33);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 31) | ((values[valuesOffset] & 0xffffffffL) >>> 4);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 60) | ((values[valuesOffset++] & 0xffffffffL) << 25) | ((values[valuesOffset] & 0xffffffffL) >>> 10);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 54) | ((values[valuesOffset++] & 0xffffffffL) << 19) | ((values[valuesOffset] & 0xffffffffL) >>> 16);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 48) | ((values[valuesOffset++] & 0xffffffffL) << 13) | ((values[valuesOffset] & 0xffffffffL) >>> 22);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 42) | ((values[valuesOffset++] & 0xffffffffL) << 7) | ((values[valuesOffset] & 0xffffffffL) >>> 28);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 36) | ((values[valuesOffset++] & 0xffffffffL) << 1) | ((values[valuesOffset] & 0xffffffffL) >>> 34);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 30) | ((values[valuesOffset] & 0xffffffffL) >>> 5);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 59) | ((values[valuesOffset++] & 0xffffffffL) << 24) | ((values[valuesOffset] & 0xffffffffL) >>> 11);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 53) | ((values[valuesOffset++] & 0xffffffffL) << 18) | ((values[valuesOffset] & 0xffffffffL) >>> 17);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 47) | ((values[valuesOffset++] & 0xffffffffL) << 12) | ((values[valuesOffset] & 0xffffffffL) >>> 23);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 41) | ((values[valuesOffset++] & 0xffffffffL) << 6) | ((values[valuesOffset] & 0xffffffffL) >>> 29);
        blocks[blocksOffset++] = ((values[valuesOffset++] & 0xffffffffL) << 35) | (values[valuesOffset++] & 0xffffffffL);
      }
    }

    @Override
    public void encode(long[] values, int valuesOffset, long[] blocks, int blocksOffset, int iterations) {
      assert blocksOffset + iterations * blockCount() <= blocks.length;
      assert valuesOffset + iterations * valueCount() <= values.length;
      for (int i = 0; i < iterations; ++i) {
        blocks[blocksOffset++] = (values[valuesOffset++] << 29) | (values[valuesOffset] >>> 6);
        blocks[blocksOffset++] = (values[valuesOffset++] << 58) | (values[valuesOffset++] << 23) | (values[valuesOffset] >>> 12);
        blocks[blocksOffset++] = (values[valuesOffset++] << 52) | (values[valuesOffset++] << 17) | (values[valuesOffset] >>> 18);
        blocks[blocksOffset++] = (values[valuesOffset++] << 46) | (values[valuesOffset++] << 11) | (values[valuesOffset] >>> 24);
        blocks[blocksOffset++] = (values[valuesOffset++] << 40) | (values[valuesOffset++] << 5) | (values[valuesOffset] >>> 30);
        blocks[blocksOffset++] = (values[valuesOffset++] << 34) | (values[valuesOffset] >>> 1);
        blocks[blocksOffset++] = (values[valuesOffset++] << 63) | (values[valuesOffset++] << 28) | (values[valuesOffset] >>> 7);
        blocks[blocksOffset++] = (values[valuesOffset++] << 57) | (values[valuesOffset++] << 22) | (values[valuesOffset] >>> 13);
        blocks[blocksOffset++] = (values[valuesOffset++] << 51) | (values[valuesOffset++] << 16) | (values[valuesOffset] >>> 19);
        blocks[blocksOffset++] = (values[valuesOffset++] << 45) | (values[valuesOffset++] << 10) | (values[valuesOffset] >>> 25);
        blocks[blocksOffset++] = (values[valuesOffset++] << 39) | (values[valuesOffset++] << 4) | (values[valuesOffset] >>> 31);
        blocks[blocksOffset++] = (values[valuesOffset++] << 33) | (values[valuesOffset] >>> 2);
        blocks[blocksOffset++] = (values[valuesOffset++] << 62) | (values[valuesOffset++] << 27) | (values[valuesOffset] >>> 8);
        blocks[blocksOffset++] = (values[valuesOffset++] << 56) | (values[valuesOffset++] << 21) | (values[valuesOffset] >>> 14);
        blocks[blocksOffset++] = (values[valuesOffset++] << 50) | (values[valuesOffset++] << 15) | (values[valuesOffset] >>> 20);
        blocks[blocksOffset++] = (values[valuesOffset++] << 44) | (values[valuesOffset++] << 9) | (values[valuesOffset] >>> 26);
        blocks[blocksOffset++] = (values[valuesOffset++] << 38) | (values[valuesOffset++] << 3) | (values[valuesOffset] >>> 32);
        blocks[blocksOffset++] = (values[valuesOffset++] << 32) | (values[valuesOffset] >>> 3);
        blocks[blocksOffset++] = (values[valuesOffset++] << 61) | (values[valuesOffset++] << 26) | (values[valuesOffset] >>> 9);
        blocks[blocksOffset++] = (values[valuesOffset++] << 55) | (values[valuesOffset++] << 20) | (values[valuesOffset] >>> 15);
        blocks[blocksOffset++] = (values[valuesOffset++] << 49) | (values[valuesOffset++] << 14) | (values[valuesOffset] >>> 21);
        blocks[blocksOffset++] = (values[valuesOffset++] << 43) | (values[valuesOffset++] << 8) | (values[valuesOffset] >>> 27);
        blocks[blocksOffset++] = (values[valuesOffset++] << 37) | (values[valuesOffset++] << 2) | (values[valuesOffset] >>> 33);
        blocks[blocksOffset++] = (values[valuesOffset++] << 31) | (values[valuesOffset] >>> 4);
        blocks[blocksOffset++] = (values[valuesOffset++] << 60) | (values[valuesOffset++] << 25) | (values[valuesOffset] >>> 10);
        blocks[blocksOffset++] = (values[valuesOffset++] << 54) | (values[valuesOffset++] << 19) | (values[valuesOffset] >>> 16);
        blocks[blocksOffset++] = (values[valuesOffset++] << 48) | (values[valuesOffset++] << 13) | (values[valuesOffset] >>> 22);
        blocks[blocksOffset++] = (values[valuesOffset++] << 42) | (values[valuesOffset++] << 7) | (values[valuesOffset] >>> 28);
        blocks[blocksOffset++] = (values[valuesOffset++] << 36) | (values[valuesOffset++] << 1) | (values[valuesOffset] >>> 34);
        blocks[blocksOffset++] = (values[valuesOffset++] << 30) | (values[valuesOffset] >>> 5);
        blocks[blocksOffset++] = (values[valuesOffset++] << 59) | (values[valuesOffset++] << 24) | (values[valuesOffset] >>> 11);
        blocks[blocksOffset++] = (values[valuesOffset++] << 53) | (values[valuesOffset++] << 18) | (values[valuesOffset] >>> 17);
        blocks[blocksOffset++] = (values[valuesOffset++] << 47) | (values[valuesOffset++] << 12) | (values[valuesOffset] >>> 23);
        blocks[blocksOffset++] = (values[valuesOffset++] << 41) | (values[valuesOffset++] << 6) | (values[valuesOffset] >>> 29);
        blocks[blocksOffset++] = (values[valuesOffset++] << 35) | values[valuesOffset++];
      }
    }

}
