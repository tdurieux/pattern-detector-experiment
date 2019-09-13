文字列処理(String Processing)
--------------------------------------------------------------------------------

.. only:: html

 .. contents:: 目次
    :depth: 4
    :local:

|

Overview
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

| 
|

How to use
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

全角半角文字列変換
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

全角文字列に変換
''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''

| \ ``FullHalfConverter#toFullwidth``\ は、引数の文字列を全角文字列へ変換するメソッドである。
| 以下に半角文字列から全角文字列への変換方法を示す。

.. code-block:: java

   String fullwidth = DefaultFullHalf.INSTANCE.toFullwidth("ｱﾞ!A8ｶﾞザ");    // (1)

.. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
.. list-table::
   :header-rows: 1
   :widths: 10 90

   * - 項番
     - 説明
   * - | (1)
     - | \ ``org.terasoluna.gfw.common.fullhalf.DefaultFullHalf``\ クラスを使用し、デフォルトの全角文字列と半角文字列の組合せが登録された\ ``FullHalfConverter#toFullwidth``\ にて、引数に渡した文字列を、全角文字列へ変換する。
       | 本例では、"ア゛！Ａ８ガザ" に変換される。
       | なお、本例の"ザ"のように半角文字ではない（デフォルトの組合せに無い）文字は、そのまま返却される。
       | \ ``DefaultFullHalf``\ クラスが定義しているデフォルトの全角文字列と半角文字列の組合せについては `ソース <https://github.com/terasolunaorg/terasoluna-gfw/blob/master/terasoluna-gfw-string/src/main/java/org/terasoluna/gfw/common/fullhalf/DefaultFullHalf.java>`_ を参照されたい。

.. note::

    独自の全角文字列と半角文字列の組合せを登録した\ ``FullHalfConverter``\ を使用する場合は :ref:`StringOperationsHowToDesignCustomFullHalfConverter` を参照されたい。

|


半角文字列に変換
''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''

| \ ``FullHalfConverter#toHalfwidth``\ は、引数の文字列を半角文字列へ変換するメソッドである。
| 以下に全角文字列から半角文字列への変換方法を示す。

.. code-block:: java

   String halfwidth = DefaultFullHalf.INSTANCE.toHalfwidth("Ａ！アガｻ");    // (1)

.. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
.. list-table::
   :header-rows: 1
   :widths: 10 90

   * - 項番
     - 説明
   * - | (1)
     - | \ ``DefaultFullHalf``\ クラスを使用し、デフォルトの全角文字列と半角文字列の組合せが登録された\ ``FullHalfConverter#toHalfwidth``\ にて、引数に渡した文字列を、半角文字列へ変換する。
       | 本例では、"A!ｱｶﾞｻ" に変換される。
       | なお、本例の"ｻ"のように全角文字ではない（デフォルトの組合せに無い）文字は、そのまま返却される。
       | \ ``DefaultFullHalf``\ クラスが定義しているデフォルトの全角文字列と半角文字列の組合せについては `ソース <https://github.com/terasolunaorg/terasoluna-gfw/blob/master/terasoluna-gfw-string/src/main/java/org/terasoluna/gfw/common/fullhalf/DefaultFullHalf.java>`_ を参照されたい。

.. note::

    独自の全角文字列と半角文字列の組合せを登録した\ ``FullHalfConverter``\ を使用する場合は :ref:`StringOperationsHowToDesignCustomFullHalfConverter` を参照されたい。

.. note::

    \ ``FullHalfConverter``\ は、2文字以上で1文字を表現する結合文字（例："シ（\\u30b7）" + "濁点（\\u3099）"）を半角文字（例："ｼﾞ"）へ変換することが出来ない。
    このような場合、テキスト正規化を行い、結合文字を合成文字（例："ジ（\\u30b8）"）に変換してから \ ``FullHalfConverter``\ を使用する必要がある。
    
    テキスト正規化を行う場合は、\ ``java.text.Normalizer``\ を使用する。
    以下に、使用方法を示す。
    
    正規化形式 ： NFD（正準等価性によって分解する）の場合
    
      .. code-block:: java

         String str1 = Normalizer.normalize("モジ", Normalizer.Form.NFD); // str1 = "モシ + Voiced sound mark(\\u3099)"
         String str2 = Normalizer.normalize("ﾓｼﾞ", Normalizer.Form.NFD);  // str2 = "ﾓｼﾞ"

    正規化形式 ： NFC（正準等価性によって分解し、再度合成する）の場合
    
      .. code-block:: java

         String mojiStr = "モシ\\u3099";                                   // "モシ + Voiced sound mark(\\u3099)"
         String str1 = Normalizer.normalize(mojiStr, Normalizer.Form.NFC); // str1 = "モジ（\\u30b8）"
         String str2 = Normalizer.normalize("ﾓｼﾞ", Normalizer.Form.NFC);   // str2 = "ﾓｼﾞ"
    
    正規化形式 ： NFKD（互換等価性によって分解する）の場合
    
      .. code-block:: java

         String str1 = Normalizer.normalize("モジ", Normalizer.Form.NFKD); // str1 = "モシ + Voiced sound mark(\\u3099)"
         String str2 = Normalizer.normalize("ﾓｼﾞ", Normalizer.Form.NFKD);  // str2 = "モシ + Voiced sound mark(\\u3099)"
    
    正規化形式 ： NFKC（互換等価性によって分解し、再度合成する）の場合
    
      .. code-block:: java

         String mojiStr = "モシ\\u3099";                                    // "モシ + Voiced sound mark(\\u3099)"
         String str1 = Normalizer.normalize(mojiStr, Normalizer.Form.NFKC); // str1 = "モジ（\\u30b8）"
         String str2 = Normalizer.normalize("ﾓｼﾞ", Normalizer.Form.NFKC) ;  // str2 = "モジ"
    
    
    上記のように、結合文字を合成文字に変換する場合などは、正規化形式 ： NFC または NFKC を利用する。
    
    詳細は \ `JavaDoc <https://docs.oracle.com/javase/8/docs/api/java/text/Normalizer.html>`_\ を参照されたい。

|


.. _StringOperationsHowToDesignCustomFullHalfConverter:

独自の全角文字列と半角文字列の組合せを登録したFullHalfConverterクラスの作成
''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''

| \ ``DefaultFullHalf``\ を使用せず、独自に作成した全角文字列と半角文字列の組合せを使って \ ``FullHalfConverter``\ を使用することも出来る。
| 以下に、独自の全角文字列と半角文字列の組合せを使って \ ``FullHalfConverter``\ を使用する方法を示す。

* | 独自の組合せを使った \ ``FullHalfConverter``\ を提供するクラスの実装

 .. code-block:: java
 
    public class CustomFullHalf {
        
        private static final int FULL_HALF_CODE_DIFF = 0xFEE0;
        
        public static final FullHalfConverter INSTANCE;
        
        static {
            // (1)
            FullHalfPairsBuilder builder = new FullHalfPairsBuilder();
            for (char c = '!'; c <= '~'; c++) {
                String fullwidth = String.valueOf((char) (c + FULL_HALF_CODE_DIFF));
                builder.pair(fullwidth, String.valueOf(c));
            }
            
            // (2)
            builder.pair("。", "｡").pair("「", "｢").pair("」", "｣").pair("、", "､")
                    .pair("・", "･").pair("ァ", "ｧ").pair("ィ", "ｨ").pair("ゥ", "ｩ")
                    .pair("ェ", "ｪ").pair("ォ", "ｫ").pair("ャ", "ｬ").pair("ュ", "ｭ")
                    .pair("ョ", "ｮ").pair("ッ", "ｯ").pair("ア", "ｱ").pair("イ", "ｲ")
                    .pair("ウ", "ｳ").pair("エ", "ｴ").pair("オ", "ｵ").pair("カ", "ｶ")
                    .pair("キ", "ｷ").pair("ク", "ｸ").pair("ケ", "ｹ").pair("コ", "ｺ")
                    .pair("サ", "ｻ").pair("シ", "ｼ").pair("ス", "ｽ").pair("セ", "ｾ")
                    .pair("ソ", "ｿ").pair("タ", "ﾀ").pair("チ", "ﾁ").pair("ツ", "ﾂ")
                    .pair("テ", "ﾃ").pair("ト", "ﾄ").pair("ナ", "ﾅ").pair("ニ", "ﾆ")
                    .pair("ヌ", "ﾇ").pair("ネ", "ﾈ").pair("ノ", "ﾉ").pair("ハ", "ﾊ")
                    .pair("ヒ", "ﾋ").pair("フ", "ﾌ").pair("ヘ", "ﾍ").pair("ホ", "ﾎ")
                    .pair("マ", "ﾏ").pair("ミ", "ﾐ").pair("ム", "ﾑ").pair("メ", "ﾒ")
                    .pair("モ", "ﾓ").pair("ヤ", "ﾔ").pair("ユ", "ﾕ").pair("ヨ", "ﾖ")
                    .pair("ラ", "ﾗ").pair("リ", "ﾘ").pair("ル", "ﾙ").pair("レ", "ﾚ")
                    .pair("ロ", "ﾛ").pair("ワ", "ﾜ").pair("ヲ", "ｦ").pair("ン", "ﾝ")
                    .pair("ガ", "ｶﾞ").pair("ギ", "ｷﾞ").pair("グ", "ｸﾞ")
                    .pair("ゲ", "ｹﾞ").pair("ゴ", "ｺﾞ").pair("ザ", "ｻﾞ")
                    .pair("ジ", "ｼﾞ").pair("ズ", "ｽﾞ").pair("ゼ", "ｾﾞ")
                    .pair("ゾ", "ｿﾞ").pair("ダ", "ﾀﾞ").pair("ヂ", "ﾁﾞ")
                    .pair("ヅ", "ﾂﾞ").pair("デ", "ﾃﾞ").pair("ド", "ﾄﾞ")
                    .pair("バ", "ﾊﾞ").pair("ビ", "ﾋﾞ").pair("ブ", "ﾌﾞ")
                    .pair("べ", "ﾍﾞ").pair("ボ", "ﾎﾞ").pair("パ", "ﾊﾟ")
                    .pair("ピ", "ﾋﾟ").pair("プ", "ﾌﾟ").pair("ペ", "ﾍﾟ")
                    .pair("ポ", "ﾎﾟ").pair("ヴ", "ｳﾞ").pair("\u30f7", "ﾜﾞ")
                    .pair("\u30fa", "ｦﾞ").pair("゛", "ﾞ").pair("゜", "ﾟ")
                    .pair("　", " ").pair("”", "\"").pair("’", "'").pair("￥", "\\")
                    
                    // (3)
                    .pair("ー", "-");
            
            // (4)
            INSTANCE = new FullHalfConverter(builder.build());
        }
    }

 .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
 .. list-table::
    :header-rows: 1
    :widths: 10 90

    * - 項番
      - 説明
    * - | (1)
      - | \ ``org.terasoluna.gfw.common.fullhalf.FullHalfPairsBuilder``\ を使用して、全角文字列と半角文字列の組合せとなる \ ``org.terasoluna.gfw.common.fullhalf.FullHalfPairs``\ を作成する。
    * - | (2)
      - | Unicodeの全角"！"から"～"までと半角"!"から"~"までのコード値は、コード定義の順番が同じであるため、ループ処理にてマッピング可能であったが、それ以外の文字はコード定義の順番が全角文字列と半角文字列で一致しない。そのため、それぞれ個別にマッピングする。
    * - | (3)
      - | \ ``DefaultFullHalf``\ では、全角文字列"ー"に対する半角文字列を"ｰ(\uFF70)"と設定していたところを、"-(\u002D)"に変更して設定。
    * - | (4)
      - | \ ``FullHalfPairsBuilder``\ より作成した \ ``FullHalfPairs``\ を使用して、 \ ``FullHalfConverter``\ を作成する。

 .. note::

    \ ``FullHalfPairsBuilder#pair``\ メソッドは、以下の条件を満たさない場合、\ ``java.lang.IllegalArgumentException``\ をスローする。
     * 第1引数の全角文字は1文字
     * 第2引数の半角文字は1文字または2文字

|

* | 独自の組合せを使った \ ``FullHalfConverter``\ の使用例

 .. code-block:: java
 
    String halfwidth = MyProjectFullHalf.INSTANCE.toHalfwidth("ハローワールド！"); // (1)

 .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
 .. list-table::
    :header-rows: 1
    :widths: 10 90

    * - 項番
      - 説明
    * - | (1)
      - | 実装した \ ``MyProjectFullHalf``\ を使用し、 独自の組合せが登録された \ ``FullHalfConverter#toHalfwidth``\ にて、引数に渡した文字列を、半角文字列へ変換する。
        | 本例では、"ﾊﾛ-ﾜ-ﾙﾄﾞ!" （"-" は (\u002D)）に変換される。


|

文字種チェック
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

| 対象の文字列が、コードポイント集合に含まれるかどうかのチェックを行うため、以下の機能を提供する。

* コードポイント集合の作成
* コードポイント集合同士の集合演算
* コードポイント集合を使った文字列チェック
* Bean Validation との連携


コードポイント集合の作成
''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''

| \ ``org.terasoluna.gfw.common.codepoints.CodePoints``\ は、コードポイント集合を表現するクラスである。
| \ ``CodePoints``\ のインスタンスを作成することで、コードポイント集合を作成できる。
| \ ``CodePoints``\ のインスタンスの作成方法を以下に示す。

* 既存のコードポイント集合からインスタンスを作成する場合（キャッシュする）

| 既存のコードポイント集合のクラス( \ ``Class<? extends CodePoints>``\ )からインスタンスを作成し、作成したインスタンスをキャッシュする方法を以下に示す。
| 通常、特定のコードポイント集合は複数回作成する必要はないため、この方法を使用して、キャッシュすることを推奨する。

.. code-block:: java

   CodePoints codePoints = CodePoints.of(ASCIIPrintableChars.class);  // (1)

.. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
.. list-table::
:header-rows: 1
   :widths: 10 90

       * - 項番
         - 説明
       * - | (1)
         - | \ ``CodePoints#of``\ メソッドにコードポイント集合のクラスを渡すことで、インスタンスを取得出来る。
       | 本例では、 Ascii印字可能文字のコードポイント集合 \ ``org.terasoluna.gfw.common.codepoints.catalog.ASCIIPrintableChars``\ のインスタンスが取得される。
       | また、このメソッドを使用することで、作成されたインスタンスはキャッシュされる。

.. note::

     コードポイント集合のクラスは、\ ``CodePoints``\ と同じコアパッケージ内に複数存在する。その他にも、コードポイント集合を提供するプロジェクトが存在する。それらのプロジェクトは、必要に応じて自プロジェクトに追加する。
     提供されるコードポイント集合の詳細は、 :ref:`StringProcessingCodePointsList` を参照されたい。

     また、 新規にコードポイント集合を作成することも出来る。
     詳細は、 :ref:`StringProcessingCodePointsCreate` を参照されたい。

|

* 既存のコードポイント集合からインスタンスを作成する場合（キャッシュしない）

| 既存のコードポイント集合のクラスからインスタンスを作成する方法を以下に示す。
| この方法を使用した場合、作成されるインスタンスはキャッシュされないため、キャッシュすべきでない処理（集合演算の引数等）で使用することを推奨する。

.. code-block:: java

   CodePoints codePoints = new ASCIIPrintableChars();  // (1)

.. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
.. list-table::
:header-rows: 1
   :widths: 10 90

       * - 項番
         - 説明
       * - | (1)
         - | \ ``new``\ でコードポイント集合のクラスのインスタンスを取得出来る。
       | 本例では、 Ascii印字可能文字のコードポイント集合 \ ``ASCIIPrintableChars``\ のインスタンスが取得される。
       | なお、この方法で作成されたインスタンスはキャッシュされない。

|

* CodePointsからインスタンスを作成する場合

| \ ``CodePoints``\ からインスタンスを作成する方法を以下に示す。
| この方法を使用した場合、作成されるインスタンスはキャッシュされないため、キャッシュすべきでない処理（集合演算の引数等）で使用することを推奨する。

 * \ ``CodePoints``\ のコンストラクタに、コードポイント( \ ``int``\ )の可変長配列を渡す場合

   .. code-block:: java

      CodePoints codePoints = new CodePoints(0x0061 /* a */, 0x0062 /* b */);  // (1)

   .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
.. list-table::
:header-rows: 1
      :widths: 10 90

          * - 項番
            - 説明
          * - | (1)
            - | \ ``int``\ のコードポイントを、\ ``CodePoints``\ のコンストラクタに渡してインスタンスを取得出来る。
          | 本例では、 文字"a"と"b"のコードポイント集合のインスタンスが取得される。

 |

 * \ ``CodePoints``\ のコンストラクタに、コードポイント( \ ``int``\ )の \ ``Set``\ を渡す場合

   .. code-block:: java

      Set<Integet> set = new HashSet<>();
      set.add(0x0061 /* a */);
      set.add(0x0062 /* b */);
      CodePoints codePoints = new CodePoints(set);  // (1)

   .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
.. list-table::
:header-rows: 1
      :widths: 10 90

          * - 項番
            - 説明
          * - | (1)
            - | \ ``int``\ のコードポイントを \ ``Set``\ に追加し、\ ``Set``\ を \ ``CodePoints``\ のコンストラクタに渡してインスタンスを取得出来る。
          | 本例では、 文字"a"と"b"のコードポイント集合のインスタンスが取得される。

 |

 * \ ``CodePoints``\ のコンストラクタに、コードポイントを含む文字列の可変長配列を渡す場合

   .. code-block:: java

      CodePoints codePoints = new CodePoints("ab");         // (1)

      // CodePoints codePoints = new CodePoints("a", "b");  // (2)

   .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
.. list-table::
:header-rows: 1
      :widths: 10 90

          * - 項番
            - 説明
          * - | (1)
            - | コードポイントを含む文字列を \ ``CodePoints``\ のコンストラクタに渡してインスタンスを取得出来る。
          | 本例では、 文字"a"と"b"のコードポイント集合のインスタンスが取得される。
      * - | (2)
        - | 文字列を複数に分けて渡すことも出来る。(1)と同じ結果となる。


コードポイント集合同士の集合演算
''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''

| 既存のコードポイント集合から、集合演算を行い、新規のコードポイント集合のインスタンスを作成することが出来る。
| なお、集合演算によって元のコードポイント集合の状態が変更されることは無い。
| 集合演算で新規のコードポイント集合のインスタンスを作成する方法を以下に示す。


* 和集合で新規のコードポイント集合のインスタンスを作成する場合

  .. code-block:: java

     CodePoints abCp = new CodePoints(0x0061 /* a */, 0x0062 /* b */);
     CodePoints cdCp = new CodePoints(0x0063 /* c */, 0x0064 /* d */);

     CodePoints abcdCp = abCp.union(cdCp);    // (1)

  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
.. list-table::
:header-rows: 1
     :widths: 10 90

         * - 項番
           - 説明
         * - | (1)
           - | \ ``CodePoints#union``\ メソッドで、 ２つのコードポイント集合の和集合を計算し、新規のコードポイント集合のインスタンスを作成する。
         | 本例では、文字列"ab"に含まれるコードポイントの集合と、文字列"cd"に含まれるコードポイントの集合の和集合を計算し、新規のコードポイントの集合（文字列"abcd"に含まれるコードポイントの集合に相当）のインスタンスを作成している。

|

* 差集合で新規のコードポイント集合のインスタンスを作成する場合

  .. code-block:: java

     CodePoints abcdCp = new CodePoints(0x0061 /* a */, 0x0062 /* b */,
             0x0063 /* c */, 0x0064 /* d */);
     CodePoints cdCp = new CodePoints(0x0063 /* c */, 0x0064 /* d */);

     CodePoints abCp = abcdCp.subtract(cdCp);    // (1)

  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
.. list-table::
:header-rows: 1
     :widths: 10 90

         * - 項番
           - 説明
         * - | (1)
           - | \ ``CodePoints#subtract``\ メソッドで、 ２つのコードポイント集合の差集合を計算し、新規のコードポイント集合のインスタンスを作成する。
         | 本例では、文字"abcd"に含まれるコードポイントの集合と、文字"cd"に含まれるコードポイントの集合の差集合を計算し、新規のコードポイントの集合（文字列"ab"に含まれるコードポイントの集合に相当）のインスタンスを作成している。

|

* 積集合で新規のコードポイント集合のインスタンスを作成する場合

  .. code-block:: java

     CodePoints abcdCp = new CodePoints(0x0061 /* a */, 0x0062 /* b */,
             0x0063 /* c */, 0x0064 /* d */);
     CodePoints cdeCp = new CodePoints(0x0063 /* c */, 0x0064 /* d */, 0x0064 /* e */);

     CodePoints cdCp = abcdCp.intersect(cdeCp);    // (1)

  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
.. list-table::
:header-rows: 1
     :widths: 10 90

         * - 項番
           - 説明
         * - | (1)
           - | \ ``CodePoints#intersect``\ メソッドで、 ２つのコードポイント集合の積集合を計算し、新規のコードポイント集合のインスタンスを作成する。
         | 本例では、文字"abcd"に含まれるコードポイントの集合と、文字"cde"に含まれるコードポイントの集合の積集合を計算し、新規のコードポイントの集合（文字列"cd"に含まれるコードポイントの集合に相当）のインスタンスを作成している。


コードポイント集合を使った文字列チェック
''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''

| \ ``CodePoints``\ に用意されている各種チェックメソッドにて文字列チェックが出来る。
| 文字列チェックの方法を以下に示す。
|

* \ ``CodePoints#containsAll``\ メソッドで、対象の文字列が全てコードポイント集合に含まれているか判定する。

  .. code-block:: java

     CodePoints jisX208KanaCp = CodePoints.of(JIS_X_0208_Katakana.class);

     boolean result;
     result = jisX208KanaCp.containsAll("カ");     // true
     result = jisX208KanaCp.containsAll("カナ");   // true
     result = jisX208KanaCp.containsAll("カナa");  // false

|

* \ ``CodePoints#firstExcludedContPoint``\ メソッドで、対象の文字列のうち、コードポイント集合に含まれない最初のコードポイントを返す。

  .. code-block:: java

     CodePoints jisX208KanaCp = CodePoints.of(JIS_X_0208_Katakana.class);

     int result;
     result = jisX208KanaCp.firstExcludedCodePoint("カナa");  // 0x0061 (a)
     result = jisX208KanaCp.firstExcludedCodePoint("カaナ");  // 0x0061 (a)
     result = jisX208KanaCp.firstExcludedCodePoint("カナ");   // CodePoints#NOT_FOUND

|

* \ ``CodePoints#allExcludedCodePoints``\ メソッドで、対象の文字列のうち、コードポイント集合に含まれないコードポイントの \ ``Set``\ を返す。

  .. code-block:: java

     CodePoints jisX208KanaCp = CodePoints.of(JIS_X_0208_Katakana.class);

     Set<Integer> result;
     result = jisX208KanaCp.allExcludedCodePoints("カナa");  // [0x0061 (a)]
     result = jisX208KanaCp.allExcludedCodePoints("カaナb"); // [0x0061 (a), 0x0062 (b)]
     result = jisX208KanaCp.allExcludedCodePoints("カナ");   // []


Bean Validation との連携
''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''

| \ ``@ConsistOf``\ アノテーションにコードポイント集合のクラスを指定することで、そのBeanのフィールドに設定された文字列が、対象のコードポイント集合に全て含まれるかをチェック出来る。
| 以下に方法を示す。
|

* チェックに用いるコードポイント集合が一つの場合

  .. code-block:: java

     @ConsisOf(JIS_X_0208_Hiragana.class)    // (1)
     private String firstName;

  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
.. list-table::
:header-rows: 1
     :widths: 10 90

         * - 項番
           - 説明
         * - | (1)
           - | 対象のフィールドに設定された文字列が、全て JIS X 0208のひらがな であることをチェックする。

    |

    * チェックに用いるコードポイント集合が複数の場合

      .. code-block:: java

     @ConsisOf({JIS_X_0208_Hiragana.class, JIS_X_0208_Katakana.class})    // (1)
     private String firstName;

  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
.. list-table::
:header-rows: 1
     :widths: 10 90

         * - 項番
           - 説明
         * - | (1)
           - | 対象のフィールドに設定された文字列が、全て JIS X 0208のひらがな または JIS X 0208のカタカナ であることをチェックする。

      .. note::

       長さNの文字列をM個のコードポイント集合でチェックした場合、N x M回のチェック処理が発生する。文字列の長さが大きい場合は、性能劣化の要因となる恐れがある。そのため、チェックに使用するコードポイント集合の和集合となる新規コードポイント集合のクラスを作成し、そのクラスのみを指定したほうが良い。この場合、チェック処理はN回となる。


.. _StringProcessingCodePointsList:

コードポイント集合のクラスの一覧
''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''

| コードポイント集合のクラスと、使用する際に取込む必要のあるアーティファクトの情報を、以下に一覧で示す。

.. tabularcolumns:: |p{0.10\linewidth}|p{0.60\linewidth}|p{0.30\linewidth}|
.. list-table::
:header-rows: 1
   :widths: 10 60 30

       * - 項番
         - クラス名/ (パッケージ名) / 説明
         - アーティファクト情報
       * - | (1)
         - | \ ``ASCIIControlChars``\
       | ( \ ``org.terasoluna.gfw.common.codepoints.catalog``\ )
       | Ascii制御文字の集合(0x0000-0x001F、0x007F)
     - | groupId : org.terasoluna.gfw
       | artifactId : terasoluna-gfw-codepoints
       | version : ${terasoluna.gfw.version}
   * - | (2)
     - | \ ``ASCIIPrintableChars``\
       | ( \ ``org.terasoluna.gfw.common.codepoints.catalog``\ )
       | Ascii印字可能文字の集合(0x0020-0x007E)
     - | groupId : org.terasoluna.gfw
       | artifactId : terasoluna-gfw-codepoints
       | version : ${terasoluna.gfw.version}
   * - | (3)
     - | \ ``CRLF``\
       | ( \ ``org.terasoluna.gfw.common.codepoints.catalog``\ )
       | 改行コードの集合。0x000A( \ ``LINE FEED``\ )と0x000D( \ ``CARRIAGE RETURN``\ )。
     - | groupId : org.terasoluna.gfw
       | artifactId : terasoluna-gfw-codepoints
       | version : ${terasoluna.gfw.version}
   * - | (4)
     - | \ ``JIS_X_0201_Katakana``\
       | ( \ ``org.terasoluna.gfw.common.codepoints.catalog``\ )
       | JIS X 0201 の半角カナの集合。記号(｡｢｣､･)も含まれる。
     - | groupId : org.terasoluna.gfw.codepoints
       | artifactId : terasoluna-gfw-codepoints-jisx0201
       | version : ${terasoluna.gfw.version}
   * - | (5)
     - | \ ``JIS_X_0201_LatinLetters``\
       | ( \ ``org.terasoluna.gfw.common.codepoints.catalog``\ )
       | JIS X 0201 のLatin文字の集合。
     - | groupId : org.terasoluna.gfw.codepoints
       | artifactId : terasoluna-gfw-codepoints-jisx0201
       | version : ${terasoluna.gfw.version}
   * - | (6)
     - | \ ``JIS_X_0208_SpecialChars``\
       | ( \ ``org.terasoluna.gfw.common.codepoints.catalog``\ )
       | JIS X 0208 の1-2区：特殊文字の集合。
     - | groupId : org.terasoluna.gfw.codepoints
       | artifactId : terasoluna-gfw-codepoints-jisx0208
       | version : ${terasoluna.gfw.version}
   * - | (7)
     - | \ ``JIS_X_0208_LatinLetters``\
       | ( \ ``org.terasoluna.gfw.common.codepoints.catalog``\ )
       | JIS X 0208 の3区：英数字の集合。
     - | groupId : org.terasoluna.gfw.codepoints
       | artifactId : terasoluna-gfw-codepoints-jisx0208
       | version : ${terasoluna.gfw.version}
   * - | (8)
     - | \ ``JIS_X_0208_Hiragana``\
       | ( \ ``org.terasoluna.gfw.common.codepoints.catalog``\ )
       | JIS X 0208 の4区：ひらがなの集合。
     - | groupId : org.terasoluna.gfw.codepoints
       | artifactId : terasoluna-gfw-codepoints-jisx0208
       | version : ${terasoluna.gfw.version}
   * - | (9)
     - | \ ``JIS_X_0208_Katakana``\
       | ( \ ``org.terasoluna.gfw.common.codepoints.catalog``\ )
       | JIS X 0208 の5区：カタカナの集合。
     - | groupId : org.terasoluna.gfw.codepoints
       | artifactId : terasoluna-gfw-codepoints-jisx0208
       | version : ${terasoluna.gfw.version}
   * - | (10)
     - | \ ``JIS_X_0208_GreekLetters``\
       | ( \ ``org.terasoluna.gfw.common.codepoints.catalog``\ )
       | JIS X 0208 の6区：ギリシア文字の集合。
     - | groupId : org.terasoluna.gfw.codepoints
       | artifactId : terasoluna-gfw-codepoints-jisx0208
       | version : ${terasoluna.gfw.version}
   * - | (11)
     - | \ ``JIS_X_0208_CyrillicLetters``\
       | ( \ ``org.terasoluna.gfw.common.codepoints.catalog``\ )
       | JIS X 0208 の7区：キリル文字の集合。
     - | groupId : org.terasoluna.gfw.codepoints
       | artifactId : terasoluna-gfw-codepoints-jisx0208
       | version : ${terasoluna.gfw.version}
   * - | (12)
     - | \ ``JIS_X_0208_BoxDrawingChars``\
       | ( \ ``org.terasoluna.gfw.common.codepoints.catalog``\ )
       | JIS X 0208 の8区：罫線素片の集合。
     - | groupId : org.terasoluna.gfw.codepoints
       | artifactId : terasoluna-gfw-codepoints-jisx0208
       | version : ${terasoluna.gfw.version}
   * - | (13)
     - | \ ``JIS_X_0208_Kanji``\
       | ( \ ``org.terasoluna.gfw.common.codepoints.catalog``\ )
       | JIS X 208で規定される漢字6355字。第一・第二水準漢字。
     - | groupId : org.terasoluna.gfw.codepoints
       | artifactId : terasoluna-gfw-codepoints-jisx0208kanji
       | version : ${terasoluna.gfw.version}
   * - | (14)
     - | \ ``JIS_X_0213_Kanji``\
       | ( \ ``org.terasoluna.gfw.common.codepoints.catalog``\ )
       | JIS X 0213:2004で規定される漢字10050字。第一・第二・第三・第四水準漢字。
     - | groupId : org.terasoluna.gfw.codepoints
       | artifactId : terasoluna-gfw-codepoints-jisx0213kanji
       | version : ${terasoluna.gfw.version}


.. _StringProcessingCodePointsCreate:

コードポイント集合のクラスの新規作成
''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''

| コードポイント集合のクラスを新規で作成する場合、\ ``CodePoints``\ クラスを継承し、コンストラクタでコードポイント等を指定する。
| コードポイント集合のクラスを新規で作成する方法を以下に示す。
|

* コードポイントを指定して新規にコードポイント集合のクラスを作成する場合

 * 数字のみ からなるコードポイント集合の作成例

  .. code-block:: java

     public class NumberChars extends CodePoints {
         public NumberCodePoints() {
             super(0x0030 /* 0 */, 0x0031 /* 1 */, 0x0032 /* 2 */, 0x0033 /* 3 */,
                     0x0034 /* 4 */, 0x0035 /* 5 */, 0x0036 /* 6 */,
                     0x0037 /* 7 */, 0x0038 /* 8 */, 0x0039 /* 9 */);
         }
     }

|

* 既存のコードポイント集合の集合演算で新規にコードポイント集合のクラスを作成する場合

 * ひらがな と カタカナ からなる和集合を用いたコードポイント集合の作成例

   .. code-block:: java

      public class FullwidthHiraganaKatakana extends CodePoints {
          public FullwidthHiraganaKatakana() {
              super(new X_JIS_0208_Hiragana().union(new X_JIS_0208_Katakana()));
          }
      }

|

  * 記号（｡｢｣､･） を除いた 半角カタカナ からなる差集合を用いたコードポイント集合の作成例

    .. code-block:: java

       public class HalfwidthKatakana extends CodePoints {
           public HalfwidthKatakana() {
               CodePoints symbolCp = new CodePoints(0xFF61 /* ｡ */, 0xFF62 /* ｢ */,
                       0xFF63 /* ｣ */, 0xFF64 /* ､ */, 0xFF65 /* ･ */);

               super(new JIS_X_0201_Katakana().subtract(symbolCp));
           }
       }

  .. note::

       集合演算で使用するコードポイント集合（本例では \ ``X_JIS_0208_Hiragana``\ や、 \ ``X_JIS_0208_Katakana``\ 等）を、他で使用する予定がない場合、 \ ``new``\ を使い、キャッシュされないようにすべきである。
       \ ``CodePoints#of``\ メソッドを使用してキャッシュさせると、集合演算の途中計算のみで使用されるコードポイント集合がヒープに残り、メモリを圧迫してしまう。
       逆に他で使用する予定がある場合は、\ ``CodePoints#of``\ メソッドを使用して、キャッシュさせるべきである。


|

.. raw:: latex

   \newpage

