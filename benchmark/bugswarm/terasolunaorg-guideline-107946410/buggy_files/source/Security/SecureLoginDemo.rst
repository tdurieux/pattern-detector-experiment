【応用】セキュリティ対策の実装例
********************************************************************************

.. only:: html

 .. contents:: 目次
    :depth: 3
    :local:

はじめに
================================================================================

この章で説明すること
--------------------------------------------------------------------------------

* TERASOLUNA Server Framework for Java (5.x)を利用した、セキュリティ対策の実装例
* :ref:`app-description-sec` に示すサンプルアプリケーションを題材として、実装の方針とソースコードの説明を行う
  
対象読者
--------------------------------------------------------------------------------

* 本ガイドラインの内容を一通り把握していること
* 特に、Spring Securityチュートリアルを実施済みであること

.. warning::
    * この章で説明している実装方法はあくまでも一例であり、実際の開発においては個別の要件を考慮して実装する必要がある
    * セキュリティ対策の網羅的な実施を保証するものではないため、必要に応じて追加の対策を検討すること

.. _app-description-sec:

アプリケーションの説明
================================================================================

| 本章では、サンプルアプリケーションを題材として、セキュリティ対策の具体的な実装例について説明する。
| 以下に本章で実装例を解説するセキュリティ要件の一覧を示し、題材となるサンプルアプリケーションの機能、認証・認可に関する仕様を示す。
| 以降、このサンプルアプリケーションを本アプリケーションと呼び、特に本章で解説する実装の例を指して本実装例と呼ぶ。

.. _sec-requirements:

セキュリティ要件
--------------------------------------------------------------------------------

本アプリケーションが満たすセキュリティ要件の一覧を以下に示す。各分類ごとに、:ref:`implement-description` にて実装例の解説を行う。

.. tabularcolumns:: |p{0.10\linewidth}|p{0.20\linewidth}|p{0.50\linewidth}|p{0.20\linewidth}|
.. list-table::
    :header-rows: 1
    :widths: 10 20 30 40

    * - 項番
      - 分類
      - 要件
      - 概説
    * - | (1)
      - :ref:`パスワード変更の強制・促進 <password-change>`
      - 初期パスワード使用時のパスワード変更の強制
      - 初期パスワードを使用して認証成功した際に、パスワードの変更を強制する
    * - | (2)
      - :ref:`パスワード変更の強制・促進 <password-change>`
      - 期限切れ管理ユーザパスワードの変更の強制
      - 一定期間パスワードを変更していない管理ユーザに対して、次回認証成功時にパスワードの変更を強制する
    * - | (3)
      - :ref:`パスワード変更の強制・促進 <password-change>`
      - パスワード変更を促すメッセージの表示
      - 一定期間パスワードを変更していないユーザに対して、次回認証成功時にパスワードの変更を促すメッセージを表示する
    * - | (4)
      - :ref:`パスワードの品質チェック <password-strength>`
      - パスワードの最小文字数指定
      - パスワードとして設定できる文字数の最小値を指定する
    * - | (5)
      - :ref:`パスワードの品質チェック <password-strength>`
      - パスワードの文字種別指定
      - パスワード中に含めなければならない文字種別（英大文字、英小文字、数字、記号）を指定する
    * - | (6)
      - :ref:`パスワードの品質チェック <password-strength>`
      - ユーザ名を含むパスワードの禁止
      - パスワード中にアカウントのユーザ名を含めることを禁止する
    * - | (7)
      - :ref:`パスワードの品質チェック <password-strength>`
      - 管理ユーザパスワードの再使用禁止
      - 管理ユーザが、以前使用したパスワードを短期間のうちに再使用することを禁止する
    * - | (8)
      - :ref:`アカウントのロックアウト <account-lock>`
      - アカウントロックアウト
      - あるアカウントが短期間の間に一定回数以上認証に失敗した場合、そのアカウントを認証不能な状態（ロックアウト状態）にする
    * - | (9)
      - :ref:`アカウントのロックアウト <account-lock>`
      - アカウントロックアウト期間の指定
      - アカウントのロックアウト状態の継続時間を指定する
    * - | (10)
      - :ref:`アカウントのロックアウト <account-lock>`
      - 管理ユーザによるロックアウトの解除
      - 管理ユーザは任意のアカウントのロックアウト状態を解除できる
    * - | (11)
      - :ref:`最終ログイン日時の表示 <last-login>`
      - 前回ログイン日時の表示
      - あるアカウントで認証成功した後、トップ画面にそのアカウントが前回認証に成功した日時を表示する
    * - | (12)
      - :ref:`パスワード再発行のための認証情報の生成 <reissue-info-create>`
      - パスワード再発行用URLへのランダム文字列の付与
      - 不正なアクセスを防ぐため、パスワード再発行画面にアクセスするためのURLに十分に推測困難な文字列を付与する
    * - | (13)
      - :ref:`パスワード再発行のための認証情報の生成 <reissue-info-create>`
      - パスワード再発行用秘密情報の発行
      - パスワード再発行時のユーザ確認に用いるために、事前に十分に推測困難な秘密情報（ランダム文字列）を生成してユーザに配布する
    * - | (14)
      - :ref:`パスワード再発行のための認証情報の生成 <reissue-info-create>`
      - パスワード再発行用の認証情報への有効期限の設定
      - パスワード再発行画面のURLと秘密情報に有効期限を設定する
    * - | (15)
      - :ref:`パスワード再発行のための認証情報の配布 <reissue-info-delivery>`
      - パスワード再発行画面URLのメール送付
      - パスワード再発行ページにアクセスするためのURLは、アカウントの登録済みメールアドレスへ送付する
    * - | (16)
      - :ref:`パスワード再発行のための認証情報の配布 <reissue-info-delivery>`
      - パスワード再発行画面のURLと秘密情報の別配布
      - パスワード再発行画面のURLの漏えいに備え、秘密情報はメール以外の方法でユーザに配布する
    * - | (17)
      - :ref:`パスワード再発行の失敗上限回数の設定 <reissue-info-invalidate>`
      - パスワード再発行の失敗上限回数の設定
      - パスワード再発行時のユーザ確認に一定回数失敗した場合、パスワード再発行画面のURLと秘密情報を使用不能にする

機能
--------------------------------------------------------------------------------

本アプリケーションは、セキュリティチュートリアルで作成したアプリケーションに加え、以下の機能を持つ。

.. tabularcolumns:: |p{0.30\linewidth}|p{0.70\linewidth}|
.. list-table::
    :header-rows: 1
    :widths: 30 70

    * - 機能名
      - 説明
    * - パスワード変更機能
      - ログイン済みのユーザが、自分のアカウントのパスワードを変更する機能
    * - アカウントロックアウト機能・ロックアウト解除機能
      - 短期間に一定回数以上認証に失敗したアカウントを認証不能な状態にする機能、および再び認証可能な状態に戻す機能
    * - パスワード再発行機能
      - ユーザがパスワードを忘れてしまった場合に、ユーザ確認を行った後、新しいパスワードを設定できる機能

.. note::
  このアプリケーションはセキュリティ対策に関するサンプルであるため、本来は当然必要となる
  ユーザ登録の機能やパスワード以外の登録情報の更新機能を作成していない。

認証・認可に関する仕様
--------------------------------------------------------------------------------

本アプリケーションにおける、認証・認可に関する仕様についてそれぞれ以下に示す。

認証
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

* ログイン画面とパスワード再発行に使用する画面以外の画面へのアクセスには、認証が必要
* 認証に使用するための初期パスワードはアプリケーション側から払い出されるものとする

認可
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

* 「一般ユーザ」と「管理ユーザ」の二種類のロールが存在する
    * 一つのアカウントが複数のロールを持つことができる
* アカウントロックアウト解除機能は、管理ユーザの権限を持つアカウントのみが使用できる
      
設計情報
--------------------------------------------------------------------------------

画面遷移とURL一覧
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

画面遷移図を以下に示す。エラー時の画面遷移は省略している。

.. figure:: ./images/SecureLogin_page_transition.png
   :alt: Page Transition
   :width: 80%
   :align: center

.. tabularcolumns:: |p{0.20\linewidth}|p{0.50\linewidth}|p{0.30\linewidth}|
.. list-table::
    :header-rows: 1
    :widths: 20 50 30

    * - | 項番
      - | 画面名
      - | 認証・認可
    * - | (1)
      - | ログイン画面
      - | -
    * - | (2)
      - | トップ画面
      - | 要認証
    * - | (3)
      - | アカウント情報表示画面
      - | 要認証
    * - | (4)
      - | パスワード変更画面
      - | 要認証
    * - | (5)
      - | パスワード変更完了画面
      - | 要認証
    * - | (6)
      - | ロックアウト解除画面
      - | 要認証、要管理ユーザ権限
    * - | (7)
      - | ロックアウト解除完了画面
      - | 要認証、要管理ユーザ権限
    * - | (8)
      - | パスワード再発行のための認証情報生成画面
      - | -
    * - | (9)
      - | パスワード再発行のための認証情報生成完了画面
      - | -
    * - | (10)
      - | パスワード再発行画面
      - | -
    * - | (11)
      - | パスワード再発行完了画面
      - | -

URL一覧を以下に示す。

.. tabularcolumns:: |p{0.10\linewidth}|p{0.20\linewidth}|p{0.15\linewidth}|p{0.15\linewidth}|p{0.40\linewidth}|
.. list-table::
    :header-rows: 1
    :widths: 10 20 15 15 40

    * - 項番
      - プロセス名
      - HTTPメソッド
      - URL
      - 説明
    * - 1
      - ログイン画面表示
      - GET
      - /login
      - ログイン画面を表示する
    * - 2
      - ログイン
      - POST
      - /authentication
      - ログイン画面から入力されたユーザー名、パスワードを使って認証する(Spring Securityが行う)
    * - 3
      - ログアウト
      - POST
      - /logout
      - ログアウトする(Spring Securityが行う)
    * - 4
      - トップ画面表示
      - GET
      - /
      - トップ画面を表示する
    * - 5
      - アカウント情報表示
      - GET
      - /account
      - ログインユーザーのアカウント情報を表示する
    * - 6
      - パスワード変更画面表示
      - GET
      - /password?form
      - パスワード変更画面を表示する
    * - 7
      - パスワード変更
      - POST
      - /password
      - パスワード変更画面で入力された情報を使用して、アカウントのパスワードを変更する
    * - 8
      - パスワード変更完了画面表示
      - GET
      - /password?complete
      - パスワード変更完了画面を表示する
    * - 9
      - ロックアウト解除画面表示
      - GET
      - /unlock?form
      - ロックアウト解除画面を表示する
    * - 10
      - ロックアウト解除
      - POST
      - /unlock
      - ロック解除画面に入力された情報を使用してアカウントのロックアウトを解除する
    * - 11
      - ロックアウト解除完了画面表示
      - GET
      - /unlock?complete
      - ロックアウト解除完了画面を表示する
    * - 12
      - パスワード再発行のための認証情報生成画面表示
      - GET
      - /reissue/create?form
      - パスワード再発行のための認証情報生成画面を表示する
    * - 13
      - パスワード再発行のための認証情報生成
      - POST
      - /reissue/create
      - パスワード再発行のための認証情報を生成する
    * - 14
      - パスワード再発行のための認証情報生成完了画面表示
      - GET
      - /reissue/create?complete
      - パスワード再発行のための認証情報生成完了画面を表示する
    * - 15
      - パスワード再発行画面表示
      - GET
      - /reissue/resetpassword?form&username={username}&token={token}
      - 二つのリクエストパラメータを使用して、ユーザ専用のパスワード再発行画面表示を表示する
    * - 16
      - パスワード再発行
      - POST
      - /reissue/resetpassword
      - パスワード再発行画面に入力された情報を使用してパスワードを再発行する
    * - 17
      - パスワード再発行完了画面表示
      - POST
      - /reissue/resetpassword?complete
      - パスワード再発行完了画面を表示する

ER図
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

本実装例におけるER図を以下に示す。

.. figure:: ./images/SecureLogin_ER.png
   :alt: Entity-Relation Diagram
   :width: 80%
   :align: center

.. tabularcolumns:: |p{0.10\linewidth}|p{0.20\linewidth}|p{0.40\linewidth}|p{0.30\linewidth}|
.. list-table::
    :header-rows: 1
    :widths: 10 20 40 30

    * - 項番
      - エンティティ名
      - 説明
      - 属性
    * - | (1)
      - | アカウント
      - | ユーザの登録済みアカウント情報
      - | username : ユーザ名
        | password : パスワード（ハッシュ化済み）
        | firstName : 名
        | lastName : 姓
        | email : E-mailアドレス
        | roles : ロール(複数可)
    * - | (2)
      - | ロール
      - | 認可に使用する権限
      - | roleValue : ロールの識別子
        | roleLabel : ロールの表示名
    * - | (3)
      - | 認証成功イベント
      - | アカウントの最終ログイン日時を取得するために、認証成功時に残す情報
      - | username : ユーザ名
        | authenticationTimestamp : 認証成功日時
    * - | (4)
      - | 認証失敗イベント
      - | アカウントのロックアウト機能で用いるために、認証失敗時に残す情報
      - | username : ユーザ名
        | authenticationTimestamp : 認証失敗日時
    * - | (5)
      - | パスワード変更イベント
      - | パスワードの有効期限の判定等に用いるために、パスワード変更時に残す情報
      - | username : ユーザ名
        | useFrom : 変更後のパスワードの使用開始日時
        | password : 変更後のパスワード
    * - | (6)
      - | パスワード再発行用の認証情報
      - | パスワード再発行時に、ユーザの確認に用いる情報
      - | token : パスワード再発行画面のURLを一意かつ推測不能にするために用いる文字列
        | username : ユーザ名
        | secret : ユーザの確認に用いる文字列
        | experyDate : パスワード再発行用の認証情報の有効期限
    * - | (7)
      - | パスワード再発行失敗イベント
      - | パスワード再発行用の試行回数を制限するために、パスワード再発行失敗に残す情報
      - | token : パスワード再発行に失敗した際に使用したtoken
        | attemptDate : パスワード再発行を試行した日時

.. _implement-description:

実装方針とコード解説
================================================================================

| セキュリティ要件の分類ごとに、本実装例における実装の方針とコードの説明を行う。
| ここでは各分類ごとに要件の実現のために必要最小限のコード片のみを掲載している。コード全体を確認したい場合は **TODO:ここにコードへのリンクを張る** を参照すること。

.. note::

   本実装例では、ボイラープレートコードの排除のために、Lombokを使用している。Lombokについては、:doc:`../Appendix/Lombok` を参照。

.. _password-change:

パスワード変更の強制・促進
--------------------------------------------------------------------------------

実装する要件一覧
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

* :ref:`初期パスワード使用時のパスワード変更の強制 <sec-requirements>`
* :ref:`期限切れ管理ユーザパスワードの変更の強制 <sec-requirements>`
* :ref:`パスワード変更を促すメッセージの表示 <sec-requirements>`

動作イメージ
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

.. figure:: ./images/SecureLogin_change_password.png
   :alt: Page Transition
   :width: 80%
   :align: center

実装方針
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
| 本実装例では、パスワードを変更した際の履歴を「パスワード変更イベント」エンティティとしてデータベースに保存し、このパスワード変更イベントエンティティを使用して、初期パスワードの判定およびパスワードの有効期限切れの判定を行う。
| また、その判定結果に基づいてパスワード変更画面へのリダイレクトや、画面へのメッセージの表示を制御する。
| 具体的には以下の処理を実装して用いることで、要件を実現する。

* パスワード変更イベントエンティティの保存

  パスワードを変更した際に、以下の情報を持ったパスワード変更イベントエンティティをデータベースに登録する。

  * パスワードを変更したアカウントのユーザ名
  * 変更後のパスワード
  * 変更後のパスワードの使用開始日時

  .. note ::

     「パスワード変更の強制・促進」に関する要件の実現には、変更後のパスワードは使用しない。
     しかしながら、:ref:`パスワードの品質チェック <password-strength>` の要件の実現に使用するため、パスワード変更イベントエンティティに含めて保存する。

* 初期パスワード、パスワード有効期限切れの判定

  | 認証後、認証されたアカウントのパスワード変更イベントエンティティをデータベースから検索し、一件も見つからなければ初期パスワードを使用していると判断する。
  | そうでない場合には、最新のパスワード変更イベントエンティティを取得し、現在日時とパスワードの使用開始日時の差分を計算して、パスワードの有効期限が切れているかどうかの判定を行う。

* パスワード変更画面への強制リダイレクト

  パスワードの変更を強制するために、以下のいずれかに該当する場合には、パスワード変更画面以外へのリクエストが要求された際に、パスワード変更画面へリダイレクトさせる。

  * 認証済みのユーザが管理ユーザであり、かつパスワードの有効期限が切れている場合
  * 認証済みのユーザが初回パスワードを使用している場合

  \ ``org.springframework.web.servlet.handler.HandlerInterceptor`` \ を利用して、Controllerのハンドラメソッド実行前に上記の条件に該当するかどうかの判定を行う。

  .. tip ::
     
     認証後にパスワード変更画面へリダイレクトさせる方法は他にもあるが、方法によってはリダイレクト後にURLを直打ちすることでパスワード変更を避けて別画面にアクセスできてしまう可能性がある。
     \ ``HandlerInterceptor`` \を使用する方法ではハンドラメソッド実行前に処理を行うため、URLを直打ちするなどの方法で回避することはできない。

* パスワード変更を促すメッセージの表示

  Controllerの中で前述のパスワード有効期限切れ判定処理を呼び出す。判定結果をViewに渡し、Viewでメッセージの表示・非表示を切り替える。


.. tip ::

   初期パスワードやパスワード有効期限切れの判定を行う方法としては、アカウントのテーブルにカラムを追加してパスワードの最終変更日時等の情報を持たせるといった方法を採ることも可能である。
   そのような方法で実装を行う場合、アカウントのテーブルに様々な状態を判定するためのカラムが追加され、エントリが頻繁に更新されるという状況に繋がりがちである。

   本実装例では、テーブルをシンプルな状態に保ち、エントリの不要な更新を避けて単純に挿入と削除を使用することで要件を実現するために、イベントエンティティを用いる実装方針を採用している。

コード解説
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

上記の実装方針に従って実装されたコードについて順に解説する。

* パスワード変更イベントエンティティの保存

  パスワード変更時にパスワード変更イベントエンティティをデータベースに登録するための一連の実装を示す。

  * Entityの実装

    パスワード変更イベントエンティティの実装は以下の通り。

    .. code-block:: java

       package org.terasoluna.securelogin.domain.model;

       // omitted

       @Data
       public class PasswordHistory {

           private String username; // (1)

           private String password; // (2)

           private DateTime useFrom; // (3)

       }

    .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
    .. list-table::
       :header-rows: 1
       :widths: 10 90
    
       * - 項番
         - 説明
       * - | (1)
         - | パスワードを変更したアカウントのユーザ名
       * - | (2)
         - | 変更後のパスワード
       * - | (3)
         - | 変更後のパスワードの使用開始日時

  * Repositoryの実装

    データベースに対するパスワード変更イベントエンティティの登録、検索を行うためのRepositoryを以下に示す。

    .. code-block:: java

       package org.terasoluna.securelogin.domain.repository.passwordhistory;

       // omitted

       public interface PasswordHistoryRepository {

           int insert(PasswordHistory history); // (1)

           List<PasswordHistory> findByUseFrom(@Param("username") String username,  
                   @Param("useFrom") LocalDateTime useFrom); // (2)

           List<PasswordHistory> findLatestHistories(
                   @Param("username") String username, @Param("limit") int limit); // (3)

       }

    .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
    .. list-table::
       :header-rows: 1
       :widths: 10 90
    
       * - 項番
         - 説明
       * - | (1)
         - | 引数として与えられた\ ``PasswordHistory`` \ オブジェクトをデータベースのレコードとして登録するメソッド
       * - | (2)
         - | 引数として与えられたユーザ名をキーとして、パスワードの使用開始日時が指定された日付よりも新しい\ ``PasswordHistory`` \ オブジェクトを取得するメソッド
       * - | (3)
         - | 引数として与えられたユーザ名をキーとして、指定された個数の\ ``PasswordHistory`` \ オブジェクトを新しい順に取得するメソッド

    マッピングファイルは以下の通り。

    .. code-block:: xml

       <?xml version="1.0" encoding="UTF-8"?>
       <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
       "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

       <mapper
           namespace="org.terasoluna.securelogin.domain.repository.passwordhistory.PasswordHistoryRepository">

           <resultMap id="PasswordHistoryResultMap" type="PasswordHistory">
               <id property="username" column="username" />
               <id property="password" column="password" />
               <id property="useFrom" column="use_from" />
           </resultMap>

           <select id="findByUseFrom" resultMap="PasswordHistoryResultMap">
           <![CDATA[
               SELECT
                   username,
                   password,
                   use_from
               FROM
                   password_history
               WHERE
                   username = #{username} AND
                   use_from >= #{useFrom}
               ORDER BY use_from DESC
           ]]>
           </select>

           <select id="findLatestHistories" resultMap="PasswordHistoryResultMap">
           <![CDATA[
               SELECT
                   username,
                   password,
                   use_from
               FROM
                   password_history
               WHERE
                   username = #{username}
               ORDER BY use_from DESC
               LIMIT #{limit}
           ]]>
           </select>

           <insert id="insert" parameterType="PasswordHistory">
           <![CDATA[
               INSERT INTO password_history (
                   username,
                   password,
                   use_from
               ) VALUES (
                   #{username},
                   #{password},
                   #{useFrom}
               )
           ]]>
           </insert>
       </mapper>


  * Serviceの実装

    パスワード変更イベントエンティティの操作は :ref:`パスワードの品質チェック <password-strength>` においても使用する。
    そのため、以下のようにSharedServiceからRepositoryのメソッドを呼び出す。

    .. code-block:: java

       package org.terasoluna.securelogin.domain.service.passwordhistory;

       // omitted

       @Service
       @Transactional
       public class PasswordHistorySharedServiceImpl implements
               PasswordHistorySharedService {

           @Inject
           PasswordHistoryRepository passwordHistoryRepository;

           public int insert(PasswordHistory history) {
               return passwordHistoryRepository.insert(history);
           }

           @Transactional(readOnly = true)
           public List<PasswordHistory> findHistoriesByUseFrom(String username,
                   LocalDateTime useFrom) {
               return passwordHistoryRepository.findByUseFrom(username, useFrom);
           }

           @Override
           @Transactional(readOnly = true)
           public List<PasswordHistory> findLatestHistories(String username, int limit) {
               return passwordHistoryRepository.findLatestHistories(username, limit);
           }

       }

    パスワード変更時にパスワード変更イベントエンティティをデータベースに保存する処理の実装を以下に示す。

    .. code-block:: java

       package org.terasoluna.securelogin.domain.service.account;

       // omitted

       @Service
       @Transactional
       public class AccountSharedServiceImpl implements AccountSharedService {

           @Inject
           PasswordHistorySharedService passwordHistorySharedService;

           @Inject
           AccountRepository accountRepository;

           @Inject
           PasswordEncoder passwordEncoder;

           // omitted

           public boolean updatePassword(String username, String rawPassword) { // (1)
               String password = passwordEncoder.encode(rawPassword);
               boolean result = accountRepository.updatePassword(username, password); // (2)

               LocalDateTime passwordChangeDate = LocalDateTime.now();

               PasswordHistory passwordHistory = new PasswordHistory(); // (3)
               passwordHistory.setUsername(username);
               passwordHistory.setPassword(password);
               passwordHistory.setUseFrom(passwordChangeDate);
               passwordHistorySharedService.insert(passwordHistory); // (4)

               return result;
           }

           // omitted
       }

    .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
    .. list-table::
       :header-rows: 1
       :widths: 10 90
    
       * - 項番
         - 説明
       * - | (1)
         - | パスワードを変更する際に呼び出されるメソッド
       * - | (2)
         - | データベース上のパスワードを更新する処理を呼び出す。
       * - | (3)
         - | パスワード変更イベントエンティティを作成し、ユーザ名、変更後のパスワード、変更後のパスワードの使用開始日時を設定する。
       * - | (4)
         - | 作成したパスワード変更イベントエンティティをデータベースに登録する処理を呼び出す。


* 初期パスワード、パスワード有効期限切れの判定

  データベースに登録されたパスワード変更イベントエンティティを用いて、初期パスワードを使用しているかどうかの判定と、パスワードの有効期限が切れているかどうかを判定する処理の実装を以下に示す。

  .. code-block:: java

     package org.terasoluna.securelogin.domain.service.account;

     // omitted

     @Service
     @Transactional
     public class AccountSharedServiceImpl implements AccountSharedService {

         @Inject
         PasswordHistorySharedService passwordHistorySharedService;

         @Value("${security.passwordLifeTime}") // (1)
         int passwordLifeTime;

         // omitted

        @Transactional(readOnly = true)
        @Override
        @Cacheable("isInitialPassword")
        public boolean isInitialPassword(String username) { // (2)
            List<PasswordHistory> passwordHistories = passwordHistorySharedService
                    .findLatestHistories(username, 1); // (3)
            return passwordHistories.isEmpty(); // (4)
        }

        @Transactional(readOnly = true)
        @Override
        @Cacheable("isCurrentPasswordExpired")
        public boolean isCurrentPasswordExpired(String username) { // (5)
            List<PasswordHistory> passwordHistories = passwordHistorySharedService
                    .findLatestHistories(username, 1); // (6)

            if (passwordHistories.isEmpty()) { // (7)
                return true;
            }

            if (passwordHistories
                    .get(0)
                    .getUseFrom()
                    .isBefore(
                            LocalDateTime.now()
                                    .minusSeconds(passwordLifeTime))) { // (8)
                return true;
            }

            return false;
        }

     }

  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
  .. list-table::
     :header-rows: 1
     :widths: 10 90
  
     * - 項番
       - 説明
     * - | (1)
       - | プロパティファイルからパスワードが有効である期間の長さ（秒単位）を取得し、設定する。
     * - | (2)
       - | 初期パスワードを使用しているかどうかを判定し、使用している場合はtrue、そうでなければfalseを返すメソッド
     * - | (3)
       - | データベースから最新のパスワード変更イベントエンティティを一件取得する処理を呼び出す。
     * - | (4)
       - | データベースからパスワード変更イベントエンティティが取得できなかった場合に、初期パスワードを使用していると判定し、trueを返す。そうでなければfalseを返す。
     * - | (5)
       - | 現在使用中のパスワードの有効期限が切れているかどうかを判定し、切れている場合はtrue、そうでなければfalseを返すメソッド
     * - | (6)
       - | データベースから最新のパスワード変更イベントエンティティを一件取得する処理を呼び出す。
     * - | (7)
       - | データベースからパスワード変更イベントエンティティが取得できなかった場合には、パスワードの有効期限が切れていると判定し、trueを返す。
     * - | (8)
       - | パスワード変更イベントエンティティから取得したパスワードの使用開始日時と現在日時の差分が、(1)で設定したパスワード有効期間よりも大きい場合、パスワードの有効期限が切れていると判定し、trueを返す。
     * - | (9)
       - | (7), (8)のいずれの条件にも該当しない場合、パスワード有効期限内であると判定し、falseを返す。

* パスワード変更画面への強制リダイレクト

  パスワードの変更を強制するために、パスワード変更画面へリダイレクトさせる処理の実装を以下に示す。

  .. code-block:: java

     package org.terasoluna.securelogin.app.common.interceptor;

     // omitted

     public class PasswordExpirationCheckInterceptor extends
             HandlerInterceptorAdapter { // (1)

         @Inject
         AccountSharedService accountSharedService;

         @Override
         public boolean preHandle(HttpServletRequest request,
                 HttpServletResponse response, Object handler) throws IOException { // (2)
             Authentication authentication = (Authentication) request
                     .getUserPrincipal();

             if (authentication != null) {
                 Object principal = authentication.getPrincipal();
                 if (principal instanceof UserDetails) { // (3)
                     LoggedInUser userDetails = (LoggedInUser) principal; // (4)
                     if ((userDetails.getAccount().getRoles().contains(Role.ADMN) && accountSharedService
                             .isCurrentPasswordExpired(userDetails.getUsername())) // (5)
                             || accountSharedService.isInitialPassword(userDetails
                                     .getUsername())) { // (6)
                         response.sendRedirect(request.getContextPath() 
                                 + "/password?form"); // (7)
                         return false; // (8)
                     }
                 }
             }

             return true;
         }
     }

  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
  .. list-table::
     :header-rows: 1
     :widths: 10 90
  
     * - 項番
       - 説明
     * - | (1)
       - | Controllerのハンドラメソッド実行前に処理を挟み込むために、\ ``org.springframework.web.servlet.handler.HandlerInterceptorAdapter`` \を継承する。
     * - | (2)
       - | Controllerのハンドラメソッド実行前に実行されるメソッド
     * - | (3)
       - | 取得したユーザ情報が\ ``org.springframework.security.core.userdetails.UserDetails`` \のオブジェクトであるかどうかを確認する。
     * - | (4)
       - | \ ``UserDetails`` \のオブジェクトを取得する。本実装例では、\ ``UserDetails`` \の実装として\ ``LoggedInUser`` \というクラスを作成して用いている。
     * - | (5)
       - | \ ``UserDetails`` \オブジェクトからロールを取得してユーザが管理ユーザであるかどうかを判定する。その後、パスワード有効期限が切れているかどうかを判定する処理を呼び出す。二つの判定結果の論理積(And)をとる。
     * - | (6)
       - | 初回パスワードを使用しているかどうかを判定する処理を呼び出す。
     * - | (7)
       - | (5)または(6)のいずれかが真である場合、\ ``javax.servlet.http.HttpServletResponse`` \の\ ``sendRedirect`` \ メソッドを使用して、パスワード変更画面へリダイレクトさせる。
     * - | (8)
       - | 続けてControllerのハンドラメソッドが実行されることを防ぐために、falseを返す。

  上記のリダイレクト処理を有効にするための設定は以下の通り。

  **spring-mvc.xml**

  .. code-block:: xml

    <!-- omitted -->

    <mvc:interceptors>

        <!-- omitted -->

        <mvc:interceptor>
            <mvc:mapping path="/**" /> <!-- (1) -->
            <mvc:exclude-mapping path="/password/**" /> <!-- (2) -->
            <mvc:exclude-mapping path="/resources/**" />
            <mvc:exclude-mapping path="/**/*.html" />
            <bean
                class="org.terasoluna.securelogin.app.common.interceptor.PasswordExpirationCheckInterceptor" /> <!-- (3) -->
        </mvc:interceptor>

        <!-- omitted -->

    </mvc:interceptors>

    <!-- omitted -->

  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
  .. list-table::
     :header-rows: 1
     :widths: 10 90
  
     * - 項番
       - 説明
     * - | (1)
       - | "/"以下のすべてのパスに対するアクセスに\ ``HandlerInterceptor`` \を適用する。
     * - | (2)
       - | パスワード変更画面からパスワード変更画面へのリダイレクトを防ぐため、 "/password" 以下のパスは適用対象外とする。
     * - | (3)
       - | \ ``HandlerInterceptor`` \のクラスを指定する。

  .. tip::

     ここまで解説した通りの実装を行った場合、リクエスト毎にデータベースへのアクセスが発生し、初期パスワード判定とパスワード有効期限切れ判定が行われる。その結果、パフォーマンスの低下が問題となる可能性がある。

     これを防ぐためには、`Springのキャッシュ機能 <http://docs.spring.io/spring/docs/current/spring-framework-reference/html/cache.html>`_ 等を使用して、初期パスワード判定とパスワード有効期限切れ判定の結果をキャッシュするといった対策が有効である。

     キャッシュを使用する際には、パスワード変更時等のタイミングでキャッシュをクリアする必要があることに注意すること。

     また、必要に応じてキャッシュのTTL(生存時間)を設定すること。TTLは使用するキャッシュの実装によっては設定不能であることに注意。
     

* パスワード変更を促すメッセージの表示

  トップ画面にパスワード変更を促すメッセージを表示するための、Controllerの実装を以下に示す。

  .. code-block:: java

     package org.terasoluna.securelogin.app.welcome;

     // omitted

     @Controller
     public class HomeController {

         @Inject
         AccountSharedService accountSharedService;

         @RequestMapping(value = "/", method = { RequestMethod.GET,
                 RequestMethod.POST })
         public String home(@AuthenticationPrincipal LoggedInUser userDetails, // (1)
                 Model model) {

             Account account = userDetails.getAccount(); // (2)

             model.addAttribute("account", account);
             
             if(accountSharedService.isCurrentPasswordExpired(account.getUsername())){ // (3)
                 ResultMessages messages = ResultMessages.warning().add("w.sl.pe.0001");
                 model.addAttribute(messages);
             }

             // omitted        
             
             return "welcome/home";

         }

     }

  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
  .. list-table::
     :header-rows: 1
     :widths: 10 90
  
     * - 項番
       - 説明
     * - | (1)
       - | \ ``AuthenticationPrincipal`` \アノテーションを指定して、\ ``UserDetails`` \を実装した\ ``LoggedInUser`` \のオブジェクトを取得する。
     * - | (2)
       - | \ ``LoggedInUser`` \が保持しているアカウント情報を取得する。
     * - | (3)
       - | アカウント情報から取得したユーザ名を引数として、パスワードの有効期限切れ判定処理を呼び出す。判定結果がtrueの場合、プロパティファイルからメッセージを取得してModelに設定し、Viewに渡す。

  Viewの実装は以下の通り。

  **トップ画面(home.jsp)**

  .. code-block:: jsp

     <!-- omitted -->

     <body>
        <div id="wrapper">
            <span id="expiredMessage">
                <t:messagesPanel /> <!-- (1) -->
            </span>

            <!-- omitted -->

        </div>
     </body>

     <!-- omitted -->

  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
  .. list-table::
     :header-rows: 1
     :widths: 10 90
  
     * - 項番
       - 説明
     * - | (1)
       - | messagesPanelタグを用いて、Controllerから渡されたパスワード有効期限切れメッセージを表示する。

.. _password-strength:

パスワードの品質チェック
--------------------------------------------------------------------------------
実装する要件一覧
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
* :ref:`パスワードの最小文字数指定 <sec-requirements>`
* :ref:`パスワードの文字種別指定 <sec-requirements>`
* :ref:`ユーザ名を含むパスワードの禁止 <sec-requirements>`
* :ref:`管理ユーザパスワードの再使用禁止 <sec-requirements>`

動作イメージ
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

.. figure:: ./images/SecureLogin_password_validation.png
   :alt: Page Transition
   :width: 80%
   :align: center

実装方針
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
| パスワード変更時等にユーザが指定したパスワードの品質を検査するためには、 :doc:`../ArchitectureInDetail/Validation` の機能を利用することができる。本実装例ではBean Validationを用いてパスワードの品質を検査する。
| パスワードの品質として求められる要件はアプリケーションによって異なり、多岐に渡るため、パスワード入力チェック用のライブラリとして `Passay <http://www.passay.org/>`_ を利用し、必要なBean Validationのアノテーションを作成する。
| Passayの概要については :ref:`Appendix <passay_overview>` を参照。
| 具体的には以下の設定、処理を記述し、使用することで要件を実現する。

* Passayの検証規則の作成

  要件の実現に用いるために、以下の検証規則を作成する。

    * パスワード長の最小値を設定した検証規則
    * パスワードに含めなければならない文字種別を設定した検証規則
    * パスワードがユーザ名を含まないことをチェックするための検証規則
    * パスワードが過去に使用したパスワードと一致していないことをチェックするための検証規則

* Passayの検証器の作成

  上記で作成した検証規則を設定した、Passayの検証器を作成する。

* Bean Validationのアノテーションの作成

  Passayの検証器を使用してパスワードの入力チェックを行うためのアノテーションを作成する。過去に使用したパスワードを取得するためには、 :ref:`パスワード変更の強制・促進 <password-change>` で説明したパスワード変更イベントエンティティを用いる。

* パスワードの入力チェック

  作成したBean Validationアノテーションを用いて、パスワードの入力チェックを行う。

コード解説
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

上記の実装方針に従って実装されたコードについて順に解説する。Passayを用いたパスワード入力チェックについては :ref:`password_validation` にて説明する。

* Passayの検証規則の作成

  | 本実装例で使用するほとんどの検証規則は、Passayにデフォルトで用意されたクラスを利用することで定義できる。
  | しかしながら、Passayが提供するクラスでは、\ ``org.springframework.security.crypto.password.PasswordEncoder`` \でハッシュ化された過去のパスワードと比較する検証規則を定義することができない。
  | そのため、Passayが提供するクラスを拡張し、独自の検証規則のクラスを以下のように作成する必要がある。

  .. code-block:: java

     package org.terasoluna.securelogin.app.common.validation.rule;

     // omitted

     public class EncodedPasswordHistoryRule extends HistoryRule { // (1)

         PasswordEncoder passwordEncoder; // (2)

         public EncodedPasswordHistoryRule(PasswordEncoder passwordEncoder) {
             this.passwordEncoder = passwordEncoder;
         }

         @Override
         protected boolean matches(final String clearText,
                 final PasswordData.Reference reference) { // (3)
             return passwordEncoder.matches(clearText, reference.getPassword()); // (4)
         }
     }

  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
  .. list-table::
     :header-rows: 1
     :widths: 10 90
  
     * - 項番
       - 説明
     * - | (1)
       - | パスワードが過去に使用したパスワードに含まれないをチェックするための\ ``org.passay.HistoryRule`` \を拡張する。
     * - | (2) 
       - | パスワードのハッシュ化に用いている\ ``PasswordEncoder`` \ をインジェクションする。
     * - | (3)
       - | 過去のパスワードとの比較を行うメソッドをオーバーライドする。
     * - | (4)
       - | \ ``PasswordEncoder`` \ の \ ``matches`` \ メソッドを使用してハッシュ化されたパスワードとの比較を行う。

  Passayの検証規則を以下に示す通りBean定義する。

  **applicationContext.xml**

  .. code-block:: xml

     <bean id="lengthRule" class="org.passay.LengthRule"> <!-- (1) -->
         <property name="minimumLength" value="${security.passwordMinimumLength}" /> 
     </bean>
     <bean id="upperCaseRule" class="org.passay.CharacterRule"> <!-- (2) -->
         <constructor-arg name="data">
             <value type="org.passay.EnglishCharacterData">UpperCase</value>
         </constructor-arg>
         <constructor-arg name="num" value="1" />
     </bean>
     <bean id="lowerCaseRule" class="org.passay.CharacterRule"> <!-- (3) -->
         <constructor-arg name="data">
             <value type="org.passay.EnglishCharacterData">LowerCase</value>
         </constructor-arg>
         <constructor-arg name="num" value="1" />
     </bean>
     <bean id="digitRule" class="org.passay.CharacterRule"> <!-- (4) -->
         <constructor-arg name="data">
             <value type="org.passay.EnglishCharacterData">Digit</value>
         </constructor-arg>
         <constructor-arg name="num" value="1" />
     </bean>
     <bean id="specialCharacterRule" class="org.passay.CharacterRule"> <!-- (5) -->
         <constructor-arg name="data">
             <value type="org.passay.EnglishCharacterData">Special</value>
         </constructor-arg>
         <constructor-arg name="num" value="1" />
     </bean>
     <bean id="characterCharacteristicsRule" class="org.passay.CharacterCharacteristicsRule"> <!-- (6) -->
         <property name="rules">
             <list value-type="org.passay.CharacterRule">
                 <ref bean="upperCaseRule" />
                 <ref bean="lowerCaseRule" />
                 <ref bean="digitRule" />
                 <ref bean="specialCharacterRule" />
             </list>
         </property>
         <property name="numberOfCharacteristics" value="3" />
     </bean>
     <bean id="usernameRule" class="org.passay.UsernameRule" /> <!-- (7) -->
     <bean id="encodedPasswordHistoryRule"
         class="org.terasoluna.securelogin.app.common.validation.rule.EncodedPasswordHistoryRule"> <!-- (8) -->
         <constructor-arg name="passwordEncoder" ref="passwordEncoder" />
     </bean>

  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
  .. list-table::
     :header-rows: 1
     :widths: 10 90
  
     * - 項番
       - 説明
     * - | (1)
       - | パスワードの長さをチェックするための\ ``org.passay.LengthRule`` \のプロパティに、プロパティファイルから取得したパスワードの最短長を設定する。
     * - | (2) 
       - | 半角英大文字を一文字以上含むことをチェックする検証規則。パスワードに含まれる文字種別に関するチェックを行うための\ ``org.passay.CharacterRule`` \のコンストラクタに、\ ``org.passay.EnglishCharacterData.UpperCase`` \と数値の1を設定する。
     * - | (3)
       - | 半角英小文字を一文字以上含むことをチェックする検証規則。パスワードに含まれる文字種別に関するチェックを行うための\ ``org.passay.CharacterRule`` \のコンストラクタに、\ ``org.passay.EnglishCharacterData.LowerCase`` \と数値の1を設定する。
     * - | (4)
       - | 半角数字を一文字以上含むことをチェックする検証規則。パスワードに含まれる文字種別に関するチェックを行うための\ ``org.passay.CharacterRule`` \のコンストラクタに、\ ``org.passay.EnglishCharacterData.Digit`` \と数値の1を設定する。
     * - | (5)
       - | 半角記号を一文字以上含むことをチェックする検証規則。パスワードに含まれる文字種別に関するチェックを行うための\ ``org.passay.CharacterRule`` \のコンストラクタに、\ ``org.passay.EnglishCharacterData.Special`` \と数値の1を設定する。
     * - | (6)
       - | (2)-(5)の4つの検証規則のうち、3つを満たすことをチェックするための検証規則。\ ``org.passay.CharacterCharacteristicsRule`` \のプロパティに、(2)-(5)で定義したBeanのリストと、数値の3を設定する。
     * - | (7)
       - | パスワードにユーザ名が含まれていないことをチェックするための検証規則
     * - | (8)
       - | パスワードが過去に使用したものの中に含まれていないことをチェックするための検証規則

* Passayの検証器の作成

  前述したPassayの検証規則を用いて、実際に検証を行う検証器のBean定義を以下に示す。

  **applicationContext.xml**

  .. code-block:: xml

     <bean id="characteristicPasswordValidator" class="org.passay.PasswordValidator"> <!-- (1) -->
         <constructor-arg name="rules">
             <list value-type="org.passay.Rule">
                 <ref bean="lengthRule" />
                 <ref bean="characterCharacteristicsRule" />
                 <ref bean="usernameRule" />
             </list>
         </constructor-arg>
     </bean>
     <bean id="encodedPasswordHistoryValidator" class="org.passay.PasswordValidator"> <!-- (2) -->
         <constructor-arg name="rules">
             <list value-type="org.passay.Rule">
                 <ref bean="encodedPasswordHistoryRule" />
             </list>
         </constructor-arg>
     </bean>

  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
  .. list-table::
     :header-rows: 1
     :widths: 10 90
  
     * - 項番
       - 説明
     * - | (1)
       - | パスワード自体の性質を検証するための検証器。プロパティとして、\ ``LengthRule`` \, \ ``CharacterCharacteristicsRule`` \, \ ``UsernameRule`` \のBeanを設定する。
     * - | (2)
       - | 過去に使用したパスワードの履歴を使用したチェックを行うための検証器。プロパティとして\ ``EncodedPasswordHistoryRule`` \のBeanを設定する。

* Bean Validationのアノテーションの作成

  要件を実現するために、前述した検証器を使用する2つのアノテーションを作成する。

  * パスワード自体の性質を検証するアノテーション

    パスワードが最小文字列長よりも長いこと、指定した文字種別の文字を含むこと、ユーザ名を含まないことという三つの検証規則をチェックするアノテーションの実装を以下に示す。

    .. code-block:: java

       package org.terasoluna.securelogin.app.common.validation;

       // omitted

       @Documented
       @Constraint(validatedBy = { StrongPasswordValidator.class }) // (1)
       @Target({ TYPE, ANNOTATION_TYPE })
       @Retention(RUNTIME)
       public @interface StrongPassword {
           String message() default "{org.terasoluna.securelogin.app.common.validation.StrongPassword.message}";

           Class<?>[] groups() default {};

           String idPropertyName(); // (2)

           String newPasswordPropertyName(); // (3)

           @Target({ TYPE, ANNOTATION_TYPE })
           @Retention(RUNTIME)
           @Documented
           public @interface List {
               StrongPassword[] value();
           }

           Class<? extends Payload>[] payload() default {};
       }

    .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
    .. list-table::
       :header-rows: 1
       :widths: 10 90
    
       * - 項番
         - 説明
       * - | (1)
         - | アノテーション付与時に使用する\ ``ConstraintValidator`` \を指定する。
       * - | (2)
         - | ユーザ名のプロパティ名を指定するためのプロパティ。
       * - | (3)
         - | パスワードのプロパティ名を指定するためのプロパティ。

    .. code-block:: java

       package org.terasoluna.securelogin.app.common.validation;

       // omitted

       public class StrongPasswordValidator implements
               ConstraintValidator<StrongPassword, Object> {

           @Resource(name = "characteristicPasswordValidator") // (1)
           PasswordValidator characteristicPasswordValidator;

           private String usernamePropertyName;

           private String newPasswordPropertyName;

           @Override
           public void initialize(StrongPassword constraintAnnotation) {
               usernamePropertyName = constraintAnnotation.idPropertyName();
               newPasswordPropertyName = constraintAnnotation.newPasswordPropertyName();
           }

           @Override
           public boolean isValid(Object value, ConstraintValidatorContext context) {
               BeanWrapper beanWrapper = new BeanWrapperImpl(value);
               String username = (String) beanWrapper.getPropertyValue(usernamePropertyName);
               String newPassword = (String) beanWrapper
                       .getPropertyValue(newPasswordPropertyName);

               context.disableDefaultConstraintViolation();

               RuleResult result = characteristicPasswordValidator
                       .validate(PasswordData.newInstance(newPassword, username, null)); // (2)
               if (result.isValid()) { // (3)
                   return true;
               } else {
                   for (String message : characteristicPasswordValidator
                           .getMessages(result)) { // (4)
                       context.buildConstraintViolationWithTemplate(message)
                               .addPropertyNode(newPasswordPropertyName)
                               .addConstraintViolation();
                   }
                   return false;
               }
           }
       }

    .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
    .. list-table::
       :header-rows: 1
       :widths: 10 90
    
       * - 項番
         - 説明
       * - | (1)
         - | Passayの検証器をインジェクションする。
       * - | (2)
         - | パスワードとユーザ名を指定した\ ``org.passay.PasswordData`` \のインスタンスを作成し、検証器で入力チェックを行う。
       * - | (3)
         - | チェックの結果を確認し、OKならばtrueを返し、そうでなければfalseを返す。
       * - | (4)
         - | パスワード入力チェックエラーメッセージをすべて取得し、設定する。

  * 過去のパスワードとの比較を行うアノテーション

    | 管理ユーザが、以前使用したパスワードを短期間のうちに再使用していないことをチェックするアノテーションの実装を以下に示す。
    | 過去に使用したパスワードを取得するために、パスワード変更イベントエンティティを用いる。パスワード変更イベントエンティティについては :ref:`パスワード変更の強制・促進 <password-change>` を参照。

    .. code-block:: java

       package org.terasoluna.securelogin.app.common.validation;

       @Documented
       @Constraint(validatedBy = { NotReusedValidator.class }) // (1)
       @Target({ TYPE, ANNOTATION_TYPE })
       @Retention(RUNTIME)
       public @interface NotReused {
           String message() default "{org.terasoluna.securelogin.app.common.validation.NotReused.message}";

           Class<?>[] groups() default {};

           String idPropertyName(); // (2)

           String newPasswordPropertyName(); // (3)

           @Target({ TYPE, ANNOTATION_TYPE })
           @Retention(RUNTIME)
           @Documented
           public @interface List {
               NotReused[] value();
           }

           Class<? extends Payload>[] payload() default {};
       }

    .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
    .. list-table::
       :header-rows: 1
       :widths: 10 90
    
       * - 項番
         - 説明
       * - | (1)
         - | アノテーション付与時に使用する\ ``ConstraintValidator`` \を指定する。
       * - | (2)
         - | ユーザ名のプロパティ名を指定するためのプロパティ。データベースから過去に使用したパスワードを検索するために必要となる。
       * - | (3)
         - | パスワードのプロパティ名を指定するためのプロパティ。

    .. code-block:: java

       package org.terasoluna.securelogin.app.common.validation;

       // omitted

       public class NotReusedValidator implements
               ConstraintValidator<NotReused, Object> {

           @Inject
           AccountSharedService accountSharedService;

           @Inject
           PasswordHistorySharedService passwordHistorySharedService;

           @Inject
           PasswordEncoder passwordEncoder;

           @Resource(name = "encodedPasswordHistoryValidator") // (1)
           PasswordValidator encodedPasswordHistoryValidator;

           @Value("${security.passwordHistoricalCheckingCount}") // (2)
           int passwordHistoricalCheckingCount;

           @Value("${security.passwordHistoricalCheckingPeriod}") // (3)
           int passwordHistoricalCheckingPeriod;

           private String usernamePropertyName;

           private String newPasswordPropertyName;

           private String message;

           @Override
           public void initialize(NotReused constraintAnnotation) {
               usernamePropertyName = constraintAnnotation.idPropertyName();
               newPasswordPropertyName = constraintAnnotation.newPasswordPropertyName();
               message = constraintAnnotation.message();
           }

           @Override
           public boolean isValid(Object value, ConstraintValidatorContext context) {
               BeanWrapper beanWrapper = new BeanWrapperImpl(value);
               String username = (String) beanWrapper.getPropertyValue(usernamePropertyName);
               String newPassword = (String) beanWrapper
                       .getPropertyValue(newPasswordPropertyName);

               Account account = accountSharedService.findOne(username);
               String currentPassword = account.getPassword();

               context.disableDefaultConstraintViolation();
               boolean result = checkNewPasswordDifferentFromCurrentPassword(
                       newPassword, currentPassword, context); // (4)
               if (result && account.getRoles().contains(Role.ADMN)) { // (5)
                   result = checkHistoricalPassword(username, newPassword, context);
               }

               return result;
           }

           private boolean checkNewPasswordDifferentFromCurrentPassword(
                   String newPassword, String currentPassword,
                   ConstraintValidatorContext context) {
               if (!passwordEncoder.matches(newPassword, currentPassword)) {
                   return true;
               } else {
                   context.buildConstraintViolationWithTemplate(message)
                           .addPropertyNode(newPasswordPropertyName).addConstraintViolation();
                   return false;
               }
           }

           private boolean checkHistoricalPassword(String username,
                   String newPassword, ConstraintValidatorContext context) {
               LocalDateTime useFrom = LocalDateTime.now().minusMinutes(
                       passwordHistoricalCheckingPeriod);
               List<PasswordHistory> historyByTime = passwordHistorySharedService
                       .findHistoriesByUseFrom(username, useFrom);
               List<PasswordHistory> historyByCount = passwordHistorySharedService
                       .findLatestHistories(username, passwordHistoricalCheckingCount);
               List<PasswordHistory> history = historyByCount.size() > historyByTime
                       .size() ? historyByCount : historyByTime; // (6)

               List<PasswordData.Reference> historyData = new ArrayList<>();
               for (PasswordHistory h : history) {
                   historyData.add(new PasswordData.HistoricalReference(h
                           .getPassword())); // (7)
               }

               PasswordData passwordData = PasswordData.newInstance(newPassword,
                       username, historyData); // (8)
               RuleResult result = encodedPasswordHistoryValidator
                       .validate(passwordData); // (9)

               if (result.isValid()) { // (10)
                   return true;
               } else {
                   context.buildConstraintViolationWithTemplate(
                           encodedPasswordHistoryValidator.getMessages(result).get(0)) // (11)
                           .addPropertyNode(newPasswordPropertyName).addConstraintViolation();
                   return false;
               }
           }
       }

    .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
    .. list-table::
       :header-rows: 1
       :widths: 10 90
    
       * - 項番
         - 説明
       * - | (1)
         - | Passayの検証器をインジェクションする。
       * - | (2)
         - | いくつ前までのパスワードの再使用を禁止するかの閾値をプロパティファイルから取得し、インジェクションする。
       * - | (3)
         - | いつ以降使用したパスワードの再使用を禁止するかの閾値（秒数）をプロパティファイルから取得し、インジェクションする。
       * - | (4)
         - | 新しいパスワードが現在使用しているものと異なるかどうかをチェックする処理を呼び出す。このチェックは一般ユーザ・管理ユーザにかかわらず行う。
       * - | (5)
         - | 管理ユーザの場合は、新しいパスワードが過去に使用したパスワードに含まれていないかをチェックする処理を呼び出す。
       * - | (6)
         - | (2)で指定した個数分のパスワード変更イベントエンティティと、(3)で指定した期間分のパスワード変更イベントエンティティを取得し、どちらか数の多い方を以降のチェックに用いる。
       * - | (7)
         - | Passayの検証器で過去のパスワードとの比較を行うために、パスワード変更イベントエンティティからパスワードを取得し、\ ``org.passay.PasswordData.HistoricalReference`` \のリストを作成する。
       * - | (8)
         - | パスワード、ユーザ名、過去のパスワードのリストを指定した\ ``org.passay.PasswordData`` \のインスタンスを作成する。
       * - | (9)
         - | 検証器で入力チェックを行う。
       * - | (10)
         - | チェック結果を確認し、OKならばtrueを返し、そうでなければfalseを返す。
       * - | (11)
         - | パスワード入力チェックエラーメッセージを取得する。

    .. note ::

       「いくつ前までのパスワードの再使用を禁止するか」のみの設定では、短時間の間にパスワード変更を繰り返すことでパスワードを再使用することが可能となってしまう。
       これを防ぐために、本実装例では「いつ以降使用したパスワードの再使用を禁止するか」を設定して検査を行っている。

* パスワードの入力チェック

  Bean Validationアノテーションを使用してアプリケーション層で、パスワード入力チェックを行う。

  .. code-block:: java

     package org.terasoluna.securelogin.app.passwordchange;

     // omitted

     import lombok.Data;

     @Data
     @Compare(source = "newPasssword", destination = "confirmNewPassword", operator = Compare.Operator.EQUAL) // (1)
     @StrongPassword(idPropertyName = "username", newPasswordPropertyName = "newPassword") // (2)
     @NotReused(idPropertyName = "username", newPasswordPropertyName = "newPassword") // (3)
     @ConfirmOldPassword(idPropertyName = "username", oldPasswordPropertyName = "oldPassword") // (4)
     public class PasswordChangeForm {

         private String username;

         private String oldPassword;

         private String newPassword;

         private String confirmNewPassword;

     }

  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
  .. list-table::
     :header-rows: 1
     :widths: 10 90
  
     * - 項番
       - 説明
     * - | (1)
       - | 新しいパスワードの二回の入力が一致しているかをチェックするためのアノテーション。
     * - | (2)
       - | 上述した、パスワード自体の性質を検証するアノテーション
     * - | (3)
       - | 過去のパスワードとの比較を行うアノテーション
     * - | (4)
       - | 入力された現在のパスワードが正しいことをチェックするアノテーション。定義は割愛する。

  .. code-block:: java

     package org.terasoluna.securelogin.app.passwordchange;

     // omitted

     @Controller
     @RequestMapping("password")
     public class PasswordChangeController {

         @Inject
         PasswordChangeService passwordService;

         // omitted

         @RequestMapping(method = RequestMethod.POST)
         public String change(@AuthenticationPrincipal LoggedInUser userDetails,
                 @Validated PasswordChangeForm form, BindingResult bindingResult, // (1)
                 Model model) {

             if (bindingResult.hasErrors()) {
                 Account account = userDetails.getAccount();
                 model.addAttribute(account);
                 return "passwordchange/changeForm";
             }

             passwordService.updatePassword(form.getUsername(),
                     form.getNewPassword());

             return "redirect:/password?complete";
         }

         // omitted

     }

  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
  .. list-table::
     :header-rows: 1
     :widths: 10 90
  
     * - 項番
       - 説明
     * - | (1)
       - | パスワード変更時に呼び出されるハンドラメソッド。パラメータ中のFormに\ ``@Validated`` \ アノテーションを付与して、入力チェックを行う。

.. _account-lock:

アカウントのロックアウト
--------------------------------------------------------------------------------
実装する要件一覧
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
* :ref:`アカウントロックアウト <sec-requirements>`
* :ref:`アカウントロックアウト期間の指定 <sec-requirements>`
* :ref:`管理ユーザによるロックアウトの解除 <sec-requirements>`

動作イメージ
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

* アカウントロックアウト

.. figure:: ./images/SecureLogin_lockout_ss.png
   :alt: Page Transition
   :width: 80%
   :align: center

* ロックアウト解除

.. figure:: ./images/SecureLogin_unlock_ss.png
   :alt: Page Transition
   :width: 80%
   :align: center

実装方針
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
| Spring Securityでは、\ ``org.springframework.security.core.userdetails.UserDetails`` \に対してアカウントのロックアウト状態を設定することができる。
| 「ロックアウト状態である」と設定した場合、Spring Securityがその設定を読み取って\ ``org.springframework.security.authentication.LockedException`` \をthrowする。
| この機能を用いることにより、アカウントがロックアウト状態であるか否かを判定して\ ``UserDetails`` \に設定する処理のみを実装すれば、ロックアウト機能が実現できる。

| 本実装例では、認証に失敗した履歴を「認証失敗イベント」エンティティとしてデータベースに保存し、この認証失敗イベントエンティティを使用してアカウントのロックアウト状態の判定を行う。
| 具体的には以下の三つの処理を実装して用いることにより、アカウントのロックアウトに関する各要件を実現する。

* 認証失敗イベントエンティティの保存

  不正な認証情報の入力によって認証に失敗した際に、Springが発生させるイベントをハンドリングし、認証に使用したユーザ名と認証を試みた日時を認証失敗イベントエンティティとしてデータベースに登録する。

* ロックアウト状態の判定

  あるアカウントについて、現在時刻から一定以上新しい認証失敗イベントエンティティが一定個数以上存在する場合、該当アカウントはロックアウト状態であると判定する。
  認証時にこの判定処理を呼び出し、判定結果を\ ``UserDetails`` \の実装クラスに設定する。

* 管理ユーザによる認証失敗イベントエンティティの削除

  あるアカウントについて、認証失敗イベントエンティティをすべて削除する。
  認証失敗イベントエンティティが消去されると該当アカウントはロックアウト状態と判定されなくなるため、これはロックアウト解除処理に相当する。
  認証失敗イベントエンティティの消去は認可機能を用いて、管理ユーザ以外実行できないようにする。

.. warning::

   認証失敗イベントエンティティはロックアウトの判定のみを目的としているため、不要になったタイミングで消去される。
   認証ログが必要な場合は必ず別途ログを保存しておくこと。

認証失敗イベントエンティティを用いたロックアウト機能の動作例を以下の図を用いて説明する。
例として3回の認証失敗でロックアウトされるものとし、ロックアウト継続時間は10分とする。

.. figure:: ./images/SecureLogin_lockout.png
   :alt: Account Lockout
   :width: 60%
   :align: center
  
*  | 過去10分以内に、誤ったパスワードでの認証が3回試行されており、データベースには3回分の認証失敗イベントエンティティが保存されている。
   | そのため、アカウントはロックアウト状態であると判定される。
*  | データベースには3回分の認証失敗イベントエンティティが保存されている。
   | しかしながら、過去10分以内の認証失敗イベントエンティティは2回分のみであるため、ロックアウト状態ではないと判定される。

同様に、ロックアウトを解除する場合の動作例を以下の図で説明する。

.. figure:: ./images/SecureLogin_unlock.png
   :alt: Account Lockout
   :width: 60%
   :align: center

*  | 過去10分以内に、誤ったパスワードでの認証が3回試行されている。
   | その後、認証失敗イベントエンティティが消去されているため、データベースには認証失敗イベントエンティティが保存されておらず、ロックアウト状態ではないと判定される。

.. note::
   本実装例においては、認証失敗が連続していない場合でもロックアウトされることに留意する。
   すなわち、途中で認証成功した場合であっても、一定期間内に一定回数以上認証に失敗された場合にはロックアウトされる。
   また、ロックアウト継続時間の起点にも注意すること。
   
コード解説
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

* 共通部分

  本実装例において、アカウントのロックアウトに関する機能を実現するためには、データベースに対する認証失敗イベントエンティティの登録、検索、削除が共通的に必要となる。
  そのため、まずは認証失敗イベントエンティティに関するドメイン層・インフラストラクチャ層の実装を示す。
  
  * Entityの実装
  
    ユーザ名と認証試行日時を持つ認証失敗イベントエンティティの実装を以下に示す。
  
    .. code-block:: java
  
      package org.terasoluna.securelogin.domain.model;
      
      // omitted
      
      @Data
      public class FailedAuthentication implements Serializable {
        private static final long serialVersionUID = 1L;
      
        private String username; // (1)
      
        private LocalDateTime authenticationTimestamp; // (2)
      }
      
    .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
    .. list-table::
       :header-rows: 1
       :widths: 10 90
    
       * - 項番
         - 説明
       * - | (1)
         - | 認証に使用したユーザ名
       * - | (2)
         - | 認証を試行した日時

  * Repositoryの実装
  
    認証失敗イベントエンティティの検索、登録、削除のためのRepositoryを以下に示す。
  
    .. code-block:: java
  
      package org.terasoluna.securelogin.domain.repository.authenticationevent;
      
      // omitted
      
      public interface FailedAuthenticationRepository {
      
        int insert(FailedAuthentication accountAuthenticationLog); // (1)
      
        List<FailedAuthentication> findLatestEvents(
                        @Param("username") String username, @Param("count") long count); // (2)
      
        int deleteByUsername(@Param("username") String username); // (3)
      }
    
    .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
    .. list-table::
       :header-rows: 1
       :widths: 10 90
    
       * - 項番
         - 説明
       * - | (1)
         - | 引数として与えられた\ ``FailedAuthentication``\ オブジェクトをデータベースのレコードとして登録するメソッド
       * - | (2)
         - | 引数として与えられたユーザ名をキーとして、指定された個数の\ ``FailedAuthentication``\ オブジェクトを新しい順に取得するメソッド
       * - | (3)
         - | 引数として与えられたユーザ名をキーとして、認証失敗イベントエンティティのレコードを一括削除するメソッド
    
    マッピングファイルは以下の通り。
  
    .. code-block:: xml
    
      <?xml version="1.0" encoding="UTF-8"?>
      <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
      "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
     
      <mapper
        namespace="org.terasoluna.securelogin.domain.repository.authenticationevent.FailedAuthenticationRepository">
      
        <resultMap id="failedAuthenticationResultMap"
                type="FailedAuthentication">
                <id property="username" column="username" />
                <id property="authenticationTimestamp" column="authentication_timestamp" />
        </resultMap>
      
        <insert id="insert" parameterType="FailedAuthentication">
          <![CDATA[
              INSERT INTO failed_authentication (
                  username,
                  authentication_timestamp
              ) VALUES (
                #{username},
                  #{authenticationTimestamp}
              )
          ]]>
        </insert>
      
        <select id="findLatestEvents" resultMap="failedAuthenticationResultMap">
             <![CDATA[
                  SELECT
                      username,
                      authentication_timestamp
                  FROM
                      failed_authentication
                  WHERE
                      username = #{username}
                  ORDER BY authentication_timestamp DESC
                  LIMIT #{count}
             ]]>
        </select>
      
        <delete id="deleteByUsername">
           <![CDATA[
                DELETE FROM
                    failed_authentication
                WHERE
                    username = #{username}
           ]]>
        </delete>
      </mapper>
      
  * Serviceの実装
  
    作成したRepositoryのメソッドを呼び出すServiceを以下の通り定義する。
  
    .. code-block:: java

       package org.terasoluna.securelogin.domain.service.authenticationevent;

       // omitted

       @Service
       @Transactional
       public class AuthenticationEventSharedServiceImpl implements
                       AuthenticationEventSharedService {

           // omitted

           @Inject
           FailedAuthenticationRepository failedAuthenticationRepository;

           @Transactional(readOnly = true)
           @Override
           public List<FailedAuthentication> findLatestFailureEvents(
                           String username, int count) {
                   return failedAuthenticationRepository.findLatestEvents(username, count);
           }

           @Override
           public int insertFailureEvent(FailedAuthentication event) {
                   return failedAuthenticationRepository.insert(event);
           }

           @Override
           public int deleteFailureEventByUsername(String username) {
                   return failedAuthenticationRepository.deleteByUsername(username);
           }

           // omitted

       }
           
以下、実装方針に従って実装されたコードについて順に解説する。

* 認証失敗イベントエンティティの保存

  認証失敗時に発生するイベントをハンドリングして処理を行うために、\ ``@EventListener`` \アノテーションを使用する。

  .. code-block:: java

     package org.terasoluna.securelogin.domain.service.account;

     // omitted

     @Component
     public class AccountAuthenticationFailureBadCredentialsEventListener{ 

         @Inject
         AuthenticationEventSharedService authenticationEventSharedService;

         @EventListener // (1)
         public void onApplicationEvent(
                         AuthenticationFailureBadCredentialsEvent event) {

             String username = (String) event.getAuthentication().getPrincipal(); // (2)

             FailedAuthentication failureEvents = new FailedAuthentication(); // (3)
             failureEvents.setUsername(username);
             failureEvents.setAuthenticationTimestamp(LocalDateTime.now());

             authenticationEventSharedService.insertFailureEvent(failureEvents); // (4)
         }

     }
         
  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
  .. list-table::
     :header-rows: 1
     :widths: 10 90
  
     * - 項番
       - 説明
     * - | (1)
       - | \ ``@EventListener`` \アノテーションを付与することで、誤ったパスワード等の不正な認証情報によって認証が失敗した際に、\ ``onApplicationEvent`` \メソッドが実行される。
     * - | (2)
       - | \ ``AuthenticationFailureBadCredentialsEvent`` \オブジェクトから、認証に使用したユーザ名を取得する。
     * - | (3)
       - | 認証失敗イベントエンティティを作成し、ユーザ名と現在時刻を設定する。
     * - | (4)
       - | 認証失敗イベントエンティティをデータベースに登録する処理を呼び出す。

* ロックアウト状態の判定

  認証失敗イベントエンティティを用いてアカウントのロックアウト状態を判定する処理を記述する。

  .. code-block:: java

     package org.terasoluna.securelogin.domain.service.account;

     // omitted

     @Service
     @Transactional
     public class AccountSharedServiceImpl implements AccountSharedService {

         // omitted

         @Inject
         AuthenticationEventSharedService authenticationEventSharedService;

         @Value("${security.lockingDuration}") // (1)
         int lockingDuration;

         @Value("${security.lockingThreshold}") // (2)
         int lockingThreshold;

         @Transactional(readOnly = true)
         @Override
         public boolean isLocked(String username) {
             List<FailedAuthentication> failureEvents = authenticationEventSharedService
                             .findLatestFailureEvents(username, lockingThreshold); // (3)

             if (failureEvents.size() < lockingThreshold) { // (4)
                 return false;
             }

             if (failureEvents
                     .get(lockingThreshold - 1) // (5)
                     .getAuthenticationTimestamp()
                     .isBefore(
                             LocalDateTime.now().minusSeconds(lockingDuration))) {
                 return false;
             }

             return true;
         }

         // omitted
     }

  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
  .. list-table::
     :header-rows: 1
     :widths: 10 90
  
     * - 項番
       - 説明
     * - | (1)
       - | ロックアウトの継続時間を秒単位で指定する。プロパティファイルに定義された値をインジェクションしている。
     * - | (2)
       - | ロックアウトの閾値を指定する。ここで指定した回数だけ認証に失敗すると、アカウントがロックアウトされる。プロパティファイルに定義された値をインジェクションしている。
     * - | (3)
       - | 認証失敗イベントエンティティを、ロックアウトの閾値と同じ数だけ新しい順に取得する。
     * - | (4)
       - | 取得した認証失敗イベントエンティティの個数がロックアウトの閾値より小さい場合、ロックアウト状態ではないと判定する。
     * - | (5)
       - | 取得した認証失敗イベントエンティティのうち最も古い認証失敗時刻と現在時刻の差分が、ロックアウト継続時間よりも大きい場合には、ロックアウト状態ではないと判定する。

  | \ ``UserDetails`` \の実装クラスである\ ``org.springframework.security.core.userdetails.User`` \では、コンストラクタにロックアウト状態を渡すことができる。
  | 本実装例では以下のように\ ``User`` \を継承したクラスと、\ ``org.springframework.security.core.userdetails.UserDetailsService`` \を実装したクラスを用いる。

  .. code-block:: java
  
     package org.terasoluna.securelogin.domain.service.userdetails;

     // omitted

     public class LoggedInUser extends User {

        // omitted

        private final Account account;

        public LoggedInUser(Account account, boolean isLocked,
                        LocalDateTime lastLoginDate, List<SimpleGrantedAuthority> authorities) {
            super(account.getUsername(), account.getPassword(), true, true, true,
                        !isLocked, authorities); // (1)
            this.account = account;

            // omitted
        }

         public Account getAccount() {
             return account;
         }

        // omitted
     }

  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
  .. list-table::
     :header-rows: 1
     :widths: 10 90

     * - 項番
       - 説明
     * - | (1)
       - | 親クラスである\ ``User`` \のコンストラクタに **ロックアウト状態でないかどうか** を真理値で渡す。ロックアウト状態でない場合にtrueを渡す必要があることに注意する。

  .. code-block:: java

     package org.terasoluna.securelogin.domain.service.userdetails;

     // omitted

     @Service
     public class LoggedInUserDetailsService implements UserDetailsService {

         @Inject
         AccountSharedService accountSharedService;

         @Transactional(readOnly = true)
         @Override
         public UserDetails loadUserByUsername(String username)
                 throws UsernameNotFoundException {
             try {
                Account account = accountSharedService.findOne(username);
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                for (Role role : account.getRoles()) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_"
                            + role.getRoleValue()));
                }
                return new LoggedInUser(account,
                        accountSharedService.isLocked(username), // (1)
                        accountSharedService.getLastLoginDate(username),
                        authorities);
             } catch (ResourceNotFoundException e) {
                 throw new UsernameNotFoundException("user not found", e);
             }
         }

     }

  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
  .. list-table::
     :header-rows: 1
     :widths: 10 90

     * - 項番
       - 説明
     * - | (1)
       - | \ ``LoggedInUser`` \のコンストラクタに、\ ``isLocked`` \メソッドによるロックアウト状態の判定結果を渡す。

  作成した\ ``UserDetailsService`` \を使用するための設定は以下の通り。

  **spring-security.xml**

  .. code-block:: xml

    <!-- omitted -->
  
    <sec:authentication-manager>
        <sec:authentication-provider
            user-service-ref="loggedInUserDetailsService"> <!-- (1) -->
            <sec:password-encoder ref="passwordEncoder" />
        </sec:authentication-provider>
    </sec:authentication-manager>
    
    <!-- omitted -->
  
  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
  .. list-table::
     :header-rows: 1
     :widths: 10 90

     * - 項番
       - 説明
     * - | (1)
       - | \ ``UserDetailsService`` \のBeanのidを指定する。

* 管理ユーザによる認証失敗イベントエンティティの削除

  | ロックアウト状態の判定に認証失敗イベントエンティティを使用しているため、認証失敗イベントエンティティの削除はロックアウトの解除に相当する。
  | 認証失敗イベントエンティティの削除に関するインフラストラクチャ層・ドメイン層の実装は既に済ませているため、ここでは認可の設定と、ロックアウト解除機能としてのドメイン層・アプリケーション層の実装を行う。

  * 認可の設定

    ロックアウトの解除を行うことができるユーザの権限を以下の通りに設定する。

    **spring-security.xml**

    .. code-block:: xml

      <!-- omitted -->

        <sec:http pattern="/resources/**" security="none" />
        <sec:http>
        
            <!-- omitted -->
            
            <sec:intercept-url pattern="/unlock/**" access="hasRole('ADMN')" /> <!-- (1) -->
            
            <!-- omitted -->
            
        </sec:http>

      <!-- omitted -->

    .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
    .. list-table::
       :header-rows: 1
       :widths: 10 90
  
       * - 項番
         - 説明
       * - | (1)
         - | /unlock 以下のURLへのアクセス権限を管理ユーザに限定する。

  * Serviceの実装

    .. code-block:: java

       package org.terasoluna.securelogin.domain.service.unlock;

       // omitted

       @Transactional
       @Service
       public class UnlockServiceImpl implements UnlockService {

           @Inject
           AccountSharedService accountSharedService;

           @Inject
           AuthenticationEventSharedService authenticationEventSharedService;

           @Override
           public boolean unlock(String username) {
               if (!accountSharedService.isLocked(username)) { // (1)
                   throw new BusinessException(ResultMessages.error().add(
                           MessageKeys.E_SL_UL_5001));
               }

               authenticationEventSharedService
                      .deleteFailureEventByUsername(username); // (2)

               return true;
           }

       }
      
    .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
    .. list-table::
       :header-rows: 1
       :widths: 10 90
  
       * - 項番
         - 説明
       * - | (1)
         - | ロック解除対象のアカウントのロックアウト状態を判定し、ロックアウト状態でなければ例外を発生させる。
       * - | (2)
         - | 認証失敗イベントエンティティを消去することによりロックアウト状態を解除する。

  * Formの実装

    .. code-block:: java

      package org.terasoluna.securelogin.app.unlock;    

      @Data
      public class UnlockForm {
          @NotEmpty
          private String username;
      }
      
  * Viewの実装

    **トップ画面(home.jsp)**

    .. code-block:: jsp

      <!-- omitted -->

      <body>
          <div id="wrapper">

              <!-- omitted -->        

              <sec:authorize url="/unlock"> <!-- (1) -->
                  <form:form
                      action="${f:h(pageContext.request.contextPath)}/unlock?form">
                      <button id="unlock">Unlock Account</button>
                  </form:form>
              </sec:authorize>

              <!-- omitted -->

          </div>
      </body>

      <!-- omitted -->

    .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
    .. list-table::
       :header-rows: 1
       :widths: 10 90
  
       * - 項番
         - 説明
       * - | (1)
         - | /unlock 以下のアクセス権限を持つユーザに対してのみ表示する。

    **ロックアウト解除フォーム(unlokcForm.jsp)**

    .. code-block:: jsp
    
      <!-- omitted -->

      <body>
          <div id="wrapper">
              <h1>Unlock Account</h1>
              <t:messagesPanel />
              <form:form action="${f:h(pageContext.request.contextPath)}/unlock"
                  method="POST" modelAttribute="unlockForm">
                  <table>
                      <tr>
                          <th><form:label path="username" cssErrorClass="error-label">Username</form:label>
                          </th>
                          <td><form:input path="username" cssErrorClass="error-input" /></td>
                          <td><form:errors path="username" cssClass="error-messages" /></td>
                      </tr>
                  </table>

                  <input id="submit" type="submit" value="Unlock" />
              </form:form>
              <a href="${f:h(pageContext.request.contextPath)}/">go to Top</a>
          </div>
      </body>

      <!-- omitted -->

    **ロックアウト解除完了画面(unlockComplete.jsp)**

    .. code-block:: jsp

      <!-- omitted -->

      <body>
          <div id="wrapper">
                <h1>${f:h(username)}'s account was successfully unlocked.</h1>
                <a href="${f:h(pageContext.request.contextPath)}/">go to Top</a>
          </div>
      </body>
      
      <!-- omitted -->

  * Controllerの実装

    .. code-block:: java

       package org.terasoluna.securelogin.app.unlock;

       // omitted

       @Controller
       @RequestMapping("/unlock") // (1)
       public class UnlockController {

           @Inject
           UnlockService unlockService;

           @RequestMapping(params = "form")
           public String showForm(UnlockForm form) {
               return "unlock/unlockForm";
           }

           @RequestMapping(method = RequestMethod.POST)
           public String unlock(@Validated UnlockForm form,
                   BindingResult bindingResult, Model model,
                   RedirectAttributes attributes) {
               if (bindingResult.hasErrors()) {
                       return showForm(form);
               }

               try {
                   unlockService.unlock(form.getUsername()); // (2)
                   attributes.addFlashAttribute("username", form.getUsername());
                   return "redirect:/unlock?complete";
               } catch (BusinessException e) {
                   model.addAttribute(e.getResultMessages());
                   return showForm(form);
               }
           }

           @RequestMapping(method = RequestMethod.GET, params = "complete")
           public String unlockComplete() {
               return "unlock/unlockComplete";
           }

       }

    .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
    .. list-table::
       :header-rows: 1
       :widths: 10 90
  
       * - 項番
         - 説明
       * - | (1)
         - | /unlock 以下のURLにマッピングする。認可の設定によって、管理ユーザのみがアクセス可能となる。
       * - | (2)
         - | Formから取得したユーザ名を引数として、アカウントのロックアウトを解除する処理を呼び出す。

.. _last-login:

最終ログイン日時の表示
--------------------------------------------------------------------------------
実装する要件一覧
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
* :ref:`前回ログイン日時の表示 <sec-requirements>`

動作イメージ
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

.. figure:: ./images/SecureLogin_last_login.png
   :alt: Page Transition
   :width: 80%
   :align: center

実装方針
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
| 本実装例では、認証に成功した履歴を「認証成功イベント」エンティティとしてデータベースに保存し、この認証成功イベントエンティティを用いて、トップ画面にアカウントの前回ログイン日時を表示する。
| 具体的には以下の二つの処理を実装することで、要件を実現する。

* 認証成功イベントエンティティの保存

  認証に成功した際にSpringが発生させるイベントをハンドリングし、認証に使用したユーザ名と認証に成功した日時を認証成功イベントエンティティとしてデータベースに登録する。

* 前回ログイン日時の取得と表示

  認証時に、アカウントにおける最新の認証成功イベントエンティティをデータベースから取得し、イベントエンティティから認証成功日時を取得して\ ``org.springframework.security.core.userdetails.UserDetails`` \に設定する。
  jspから\ ``UserDetails`` \が保持している認証成功日時にアクセスし、フォーマットして表示する。

コード解説
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

* 共通部分

  本実装例において、前回ログイン日時を表示するためには、データベースに対する認証成功イベントエンティティの登録、検索が必要となる。
  そのため、まずは認証成功イベントエンティティに関するドメイン層・インフラストラクチャ層の実装から解説を行う。
  
  * Entityの実装
  
    ユーザ名と認証成功日時を持つ認証成功イベントエンティティの実装は以下の通り。
  
    .. code-block:: java
  
       package org.terasoluna.securelogin.domain.model;

       // omitted

       @Data
       public class SuccessfulAuthentication implements Serializable {

           private static final long serialVersionUID = 1L;

           private String username; // (1)

           private LocalDateTime authenticationTimestamp; // (2)

       }
    
    .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
    .. list-table::
       :header-rows: 1
       :widths: 10 90
    
       * - 項番
         - 説明
       * - | (1)
         - | 認証に使用したユーザ名
       * - | (2)
         - | 認証を試行した日時

  * Repositoryの実装
  
    認証成功イベントエンティティの検索、登録を行うためのRepositoryを以下に示す。
  
    .. code-block:: java
                  
       package org.terasoluna.securelogin.domain.repository.authenticationevent;

       // omitted

       public interface SuccessfulAuthenticationRepository {

           int insert(SuccessfulAuthentication accountAuthenticationLog); // (1)

           List<SuccessfulAuthentication> findLatestEvents(
                  @Param("username") String username, @Param("count") long count); // (2)
       }
      
    .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
    .. list-table::
       :header-rows: 1
       :widths: 10 90
    
       * - 項番
         - 説明
       * - | (1)
         - | 引数として与えられた\ ``SuccessfulAuthentication``\ オブジェクトをデータベースのレコードとして登録するメソッド
       * - | (2)
         - | 引数として与えられたユーザ名をキーとして、指定された個数の\ ``SuccessfulAuthentication``\ オブジェクトを新しい順に取得するメソッド
  
    マッピングファイルは以下の通り。
  
    .. code-block:: xml
  
       <?xml version="1.0" encoding="UTF-8"?>
       <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
       "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

       <mapper
           namespace="org.terasoluna.securelogin.domain.repository.authenticationevent.SuccessfulAuthenticationRepository">

           <resultMap id="successfulAuthenticationResultMap"
                   type="SuccessfulAuthentication">
               <id property="username" column="username" />
               <id property="authenticationTimestamp" column="authentication_timestamp" />
           </resultMap>

           <insert id="insert" parameterType="SuccessfulAuthentication">
           <![CDATA[
               INSERT INTO successful_authentication (
                   username,
                   authentication_timestamp
               ) VALUES (
                       #{username},
                   #{authenticationTimestamp}
               )
           ]]>
           </insert>

           <select id="findLatestEvents" resultMap="successfulAuthenticationResultMap">
           <![CDATA[
               SELECT
                   username,
                   authentication_timestamp
               FROM
                   successful_authentication
               WHERE
                   username = #{username}
               ORDER BY authentication_timestamp DESC
               LIMIT #{count}
           ]]>
           </select>
       </mapper>
      
  * Serviceの実装
  
    作成したRepositoryのメソッドを呼び出すServiceを以下に示す。
  
    .. code-block:: java
    
       package org.terasoluna.securelogin.domain.service.authenticationevent;

       // omitted

       @Service
       @Transactional
       public class AuthenticationEventSharedServiceImpl implements
       		AuthenticationEventSharedService {

           // omitted
           @Inject
           SuccessfulAuthenticationRepository successAuthenticationRepository;

           @Transactional(readOnly = true)
           @Override
           public List<SuccessfulAuthentication> findLatestSuccessEvents(
                           String username, int count) {
               return successAuthenticationRepository.findLatestEvents(username, count);
           }

           @Override
           public int insertSuccessEvent(SuccessfulAuthentication event) {
               return successAuthenticationRepository.insert(event);
           }

       }
  
以下、実装方針に従って実装されたコードについて順に解説する。

* 認証成功イベントエンティティの保存

  認証成功時に発生するイベントをハンドリングして処理を行うために、\ ``@EventListener`` \アノテーションを使用する。

  .. code-block:: java

     package org.terasoluna.securelogin.domain.service.account;

     // omitted

     @Component
     public class AccountAuthenticationSuccessEventListener{

         @Inject
         AuthenticationEventSharedService authenticationEventSharedService;

         @EventListener // (1)
         public void onApplicationEvent(AuthenticationSuccessEvent event) {
             LoggedInUser details = (LoggedInUser) event.getAuthentication()
                             .getPrincipal(); // (2)

             SuccessfulAuthentication successEvent = new SuccessfulAuthentication(); // (3)
             successEvent.setUsername(details.getUsername());
             successEvent.setAuthenticationTimestamp(LocalDateTime.now());

             authenticationEventSharedService.insertSuccessEvent(successEvent); // (4)
         }

     }

  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
  .. list-table::
     :header-rows: 1
     :widths: 10 90
  
     * - 項番
       - 説明
     * - | (1)
       - | \ ``@EventListener`` \アノテーションを付与することで、認証が成功した際に、\ ``onApplicationEvent`` \メソッドが実行される。
     * - | (2)
       - | \ ``AuthenticationSuccessEvent`` \オブジェクトから、\ ``UserDetails`` \の実装クラスを取得する。このクラスについては以降で説明する。
     * - | (3)
       - | 認証成功イベントエンティティを作成し、ユーザ名と現在時刻を設定する。
     * - | (4)
       - | 認証成功イベントエンティティをデータベースに登録する処理を呼び出す。

* 前回ログイン日時の取得と表示

  認証成功イベントエンティティから前回ログイン日時を取得するためのServiceを以下に示す。

   .. code-block:: java

      package org.terasoluna.securelogin.domain.service.account;

      // omitted

      @Service
      @Transactional
      public class AccountSharedServiceImpl implements AccountSharedService {

          // omitted

          @Inject
          AuthenticationEventSharedService authenticationEventSharedService;

          @Transactional(readOnly = true)
          @Override
          public DateTime getLastLoginDate(String username) {
              List<SuccessfulAuthentication> events = authenticationEventSharedService
                          .findLatestSuccessEvents(username, 1); // (1)

              if (events.isEmpty()) {
                  return null; // (2)
              } else {
                  return events.get(0).getAuthenticationTimestamp(); // (3)
              }
          }

          // omitted

      }
    
  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
  .. list-table::
     :header-rows: 1
     :widths: 10 90
  
     * - 項番
       - 説明
     * - | (1)
       - | 引数として与えられたユーザ名をキーとして、最新の認証成功イベントエンティティを一件取得する。
     * - | (2)
       - | 初回ログイン時等、認証成功イベントエンティティが一件も取得できない場合にはnullを返す。
     * - | (3)
       - | 認証成功イベントエンティティから、認証日時を取得して返す。

  ログイン時に前回ログイン日時を取得して\ ``UserDetails`` \に保持させるために、以下のように\ ``User`` \を継承したクラスと、\ ``UserDetailsService`` \を実装したクラスを作成する。

  .. code-block:: java

     package org.terasoluna.securelogin.domain.service.userdetails;

     // omitted

     public class LoggedInUser extends User {

         private final Account account;

         private final LocalDateTime lastLoginDate; // (1)

         public LoggedInUser(Account account, boolean isLocked,
                         LocalDateTime lastLoginDate, List<SimpleGrantedAuthority> authorities) {

             super(account.getUsername(), account.getPassword(), true, true, true,
                             !isLocked, authorities);
             this.account = account;
             this.lastLoginDate = lastLoginDate; // (2)
         }

         // omitted    

         public LocalDateTime getLastLoginDate() { // (3)
             return lastLoginDate;
         }

     }
    
  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
  .. list-table::
     :header-rows: 1
     :widths: 10 90
  
     * - 項番
       - 説明
     * - | (1)
       - | 前回ログイン日時を保持するためのフィールドを宣言する。
     * - | (2)
       - | 引数として与えられた前回ログイン日時をフィールドに設定する。
     * - | (3)
       - | 保持している前回ログイン日時を返すメソッド

  .. code-block:: java

     package org.terasoluna.securelogin.domain.service.userdetails;

     // omitted

     @Service
     public class LoggedInUserDetailsService implements UserDetailsService {

         @Inject
         AccountSharedService accountSharedService;

         @Transactional(readOnly = true)
         @Override
         public UserDetails loadUserByUsername(String username)
                     throws UsernameNotFoundException {
             try {
                 Account account = accountSharedService.findOne(username);
                 List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                 for (Role role : account.getRoles()) {
                         authorities.add(new SimpleGrantedAuthority("ROLE_"
                                         + role.getRoleValue()));
                 }
                 return new LoggedInUser(account,
                                 accountSharedService.isLocked(username),
                                 accountSharedService.getLastLoginDate(username), // (1)
                                 authorities);
             } catch (ResourceNotFoundException e) {
                 throw new UsernameNotFoundException("user not found", e);
             }
         }

     }

  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
  .. list-table::
     :header-rows: 1
     :widths: 10 90
  
     * - 項番
       - 説明
     * - | (1)
       - | Serviceのメソッドを呼び出して前回ログイン日時を取得し、\ ``LoggedInUser`` \のコンストラクタに渡す。

  トップ画面に前回ログイン日時を表示するためのアプリケーション層の実装を行う。

  .. code-block:: java

     package org.terasoluna.securelogin.app.welcome;

     // omitted

     @Controller
     public class HomeController {

     	@Inject
     	AccountSharedService accountSharedService;

     	@RequestMapping(value = "/", method = { RequestMethod.GET,
     			RequestMethod.POST })
     	public String home(@AuthenticationPrincipal LoggedInUser userDetails, // (1)
     			Model model) {

            // omitted
     		
     		LocalDateTime lastLoginDate = userDetails.getLastLoginDate(); // (2)
     		if (lastLoginDate != null) {
     			model.addAttribute("lastLoginDate", lastLoginDate
     					.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))); // (3)
     		}
     		
     		return "welcome/home";

     	}

     }

  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
  .. list-table::
     :header-rows: 1
     :widths: 10 90
  
     * - 項番
       - 説明
     * - | (1)
       - | \ ``@AuthenticationPrincipal`` \を使用してUserDetailsオブジェクトを取得する。
     * - | (2)
       - | \ ``LoggedInUserDetails`` \から最終ログイン日時を取得する。
     * - | (3)
       - | 最終ログイン日時をフォーマットしてModelに設定し、Viewに渡す。

  **トップ画面(home.jsp)**

  .. code-block:: jsp

    <body>
      <div id="wrapper">

          <!-- omitted -->

          <c:if test="${lastLoginDate != null}"> <!-- (1) -->
              <p id="lastLogin">
                  Last login date is ${f:h(lastLoginDate)}. <!-- (2) -->
              </p>
          </c:if>

          <!-- omitted -->

      </div>
    </body>

  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
  .. list-table::
     :header-rows: 1
     :widths: 10 90
  
     * - 項番
       - 説明
     * - | (1)
       - | 前回ログイン日時がnullの場合は表示しない。
     * - | (2)
       - | Controllerから渡された前回ログイン日時を表示する。
      
.. _reissue-info-create:

パスワード再発行のための認証情報の生成
--------------------------------------------------------------------------------
実装する要件一覧
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
* :ref:`パスワード再発行用URLへのランダム文字列の付与 <sec-requirements>`
* :ref:`パスワード再発行用秘密情報の発行 <sec-requirements>`
* :ref:`パスワード再発行用の認証情報への有効期限の設定 <sec-requirements>`
  
動作イメージ
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

.. figure:: ./images/SecureLogin_password_reissue.png
   :alt: Page Transition
   :width: 80%
   :align: center

実装方針
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
| パスワードの再発行を行う際には、再発行を行うユーザがアカウントの所有者であることを確認する必要がある。そのため、本実装例では、パスワードの再発行を行う前にパスワード再発行用の認証情報を生成する。
| 認証情報の生成の際には、パスワード再発行画面のURLに含めるためのランダムな文字列とユーザの確認を目的としたランダムな文字列を、それぞれ異なる方法で生成する。また、認証情報生成時点の日時を用いて有効期限を計算し、設定する。
| 具体的には以下の三つの処理を実装することで要件を実現する。

* パスワード再発行のための認証情報の生成と保存

  以下の4つの情報を、パスワード再発行のための認証情報としてデータベースに保存する。

  * ユーザ名：パスワードを再発行するアカウントのユーザ名
  * トークン：パスワード再発行画面のURLを、一意かつ推測不能にするために生成するランダムな文字列
  * 秘密情報：パスワード再発行時にユーザに入力させるために生成するランダムな文字列
  * 有効期限：パスワード再発行のための認証情報の有効期限

  トークンと秘密情報はそれぞれ異なる方法を使用して生成する。
  パスワード再発行のための認証情報をユーザに配布する方法については、:ref:`パスワード再発行のための認証情報の配布 <reissue-info-delivery>` を参照。

* パスワード再発行のための認証情報の有効期限の検査

  パスワード再発行画面にアクセスされた際に、リクエストパラメータに含まれるユーザ名とトークンを取得し、トークンをキーとしてデータベースに保存されているパスワード再発行のための認証情報を検索する。
  認証情報に含まれる有効期限と現在時刻を比較し、有効期限が切れていればエラー画面に遷移させる。

* パスワード再発行のための認証情報を用いたユーザの確認

  パスワードの再発行を行う際に、ユーザ名、トークンとユーザが入力した秘密情報の組み合わせがデータベース内の認証情報と一致しているかどうかを確認する。
  一致する場合にはパスワードを再発行し、不一致の場合にはエラーメッセージを表示する。

コード解説
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

* 共通部分

  上記の実装方針に従って実装を進める上で、パスワード再発行のための認証情報をデータベースに登録、検索する処理が共通的に必要となる。
  そのため、まずはパスワード再発行のための認証情報に関連するEntityとRepositoryの実装から解説する。

  * Entityの作成

    パスワード再発行のための認証情報のEntityを作成する。

    .. code-block:: java

       package org.terasoluna.securelogin.domain.model;

       // omitted

       @Data
       public class PasswordReissueInfo {

           private String username; // (1)

           private String token; // (2)

           private String secret; // (3)

           private LocalDateTime expiryDate; // (4)

       }

    .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
    .. list-table::
       :header-rows: 1
       :widths: 10 90
    
       * - 項番
         - 説明
       * - | (1)
         - | パスワード再発行対象のユーザ名
       * - | (2)
         - | パスワード再発行用URLに含めるために生成される文字列（トークン）
       * - | (3)
         - | パスワード再発行時にユーザを確認するための文字列（秘密情報）
       * - | (2)
         - | パスワード再発行のための認証情報の有効期限
           
  * Repositoryの実装

    パスワード再発行のための認証情報の検索、登録、削除を行うためのRepositoryを以下に示す。

    .. code-block:: java

       package org.terasoluna.securelogin.domain.repository.passwordreissue;

       // omitted

       public interface PasswordReissueInfoRepository {

           int insert(PasswordReissueInfo info); // (1)

           PasswordReissueInfo findOne(@Param("token") String token); // (2)

           int delete(@Param("token") String token); // (3)

           // omitted

       }

   .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
   .. list-table::
      :header-rows: 1
      :widths: 10 90
   
      * - 項番
        - 説明
      * - | (1)
        - | 引数として与えられた\ ``PasswordReissueInfo``\ オブジェクトをデータベースのレコードとして登録するメソッド
      * - | (2)
        - | 引数として与えられたトークンをキーとして、\ ``PasswordReissueInfo``\ オブジェクトを検索し、取得するメソッド
      * - | (3)
        - | 引数として与えられたトークンをキーとして、\ ``PasswordReissueInfo``\ オブジェクトを削除するメソッド

   マッピングファイルは以下の通り。

   .. code-block:: xml

      <?xml version="1.0" encoding="UTF-8"?>
      <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
      "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

      <mapper
          namespace="org.terasoluna.securelogin.domain.repository.passwordreissue.PasswordReissueInfoRepository">

          <resultMap id="PasswordReissueInfoResultMap" type="PasswordReissueInfo">
              <id property="username" column="username" />
              <id property="token" column="token" />
              <id property="secret" column="secret" />
              <id property="expiryDate" column="expiry_date" />
          </resultMap>

          <select id="findOne" resultMap="PasswordReissueInfoResultMap">
          <![CDATA[
              SELECT
                  username,
                  token,
                  secret,
                  expiry_date
              FROM
                  password_reissue_info
              WHERE
                  token = #{token}
          ]]>
          </select>

          <insert id="insert" parameterType="PasswordReissueInfo">
          <![CDATA[
              INSERT INTO password_reissue_info (
                  username,
                  token,
                  secret,
                  expiry_date
              ) VALUES (
                  #{username},
                  #{token},
                  #{secret},
                  #{expiryDate}
              )
          ]]>
          </insert>

          <delete id="delete">
          <![CDATA[
              DELETE FROM
                  password_reissue_info
              WHERE
                  token = #{token}
          ]]>
          </delete>

          <!-- omitted -->

      </mapper>

以下、実装方針に従って実装されたコードについて順に解説する。

* パスワード再発行のための認証情報の生成と保存

  * パスワード生成器の定義

    Passayのパスワード生成機能を使用するための、パスワード生成器と生成規則の定義を以下に示す。
    パスワード生成器や生成規則に関しては :ref:`password_generation` を参照。

    .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
    .. list-table::
       :header-rows: 1
       :widths: 10 90
    
       * - 項番
         - 説明
       * - | (1)
         - | Passayのパスワード生成機能で用いるパスワード生成器のBean定義
       * - | (2)
         - | Passayのパスワード生成機能で用いるパスワード生成規則のBean定義。 :ref:`password-strength` で使用した検証規則を使用し、半角英大文字、半角英小文字、半角数字をそれぞれ一文字以上含むパスワードの生成規則を定義する。

    **applicationContext.xml**

    .. code-block:: xml

       <bean id="passwordGenerator" class="org.passay.PasswordGenerator" /> <!-- (1) -->
       <bean id="passwordGenerationRules"
           class="org.springframework.beans.factory.config.ListFactoryBean">
           <property name="sourceList">
               <list value-type="org.passay.CharacterRule"> <!-- (2) -->
                   <ref bean="upperCaseRule" />
                   <ref bean="lowerCaseRule" />
                   <ref bean="digitRule" />
               </list>
           </property>
       </bean>

  * Serviceの実装

    パスワード再発行のための認証情報を作成し、データベースへ保存するための処理の実装を以下に示す。

    .. code-block:: java

       package org.terasoluna.securelogin.domain.service.passwordreissue;

       // omitted

       @Service
       @Transactionalp
       public class PasswordReissueServiceImpl implements PasswordReissueService {

           @Inject
           PasswordReissueInfoRepository passwordReissueInfoRepository;

           @Inject
           AccountSharedService accountSharedService;

           @Inject
           PasswordEncoder passwordEncoder;

           @Inject
           PasswordGenerator passwordGenerator; // (1)

           @Resource(name = "passwordGenerationRules")
           List<CharacterRule> passwordGenerationRules; //(2)

           @Value("${security.tokenLifeTime}")
           int tokenLifeTime; // (3)

           // omitted

           @Override
           public String createRawSecret() {
               return passwordGenerator.generatePassword(10, passwordGenerationRules); // (4)
           }

           @Override
           public boolean saveAndSendReissueInfo(String username, String rawSecret) {
               Account account = accountSharedService.findOne(username); // (5)
               
               String token = UUID.randomUUID().toString(); // (6)

               LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(
                       tokenLifeTime); // (7)

               PasswordReissueInfo info = new PasswordReissueInfo(); // (8)
               info.setUsername(username);
               info.setToken(token);
               info.setSecret(passwordEncoder.encode(rawSecret)); // (9)
               info.setExpiryDate(expiryDate);
               
               int count = passwordReissueInfoRepository.insert(info); // (10)

               if (count > 0) {

                   // omitted

                   return true;
               } else {
                   return false;
               }
           }

           // omitted

       }

    .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
    .. list-table::
       :header-rows: 1
       :widths: 10 90
    
       * - 項番
         - 説明
       * - | (1)
         - | Passayのパスワード生成機能で用いるパスワード生成器をインジェクションする。
       * - | (2)
         - | Passayのパスワード生成機能で用いるパスワード生成ルールをインジェクションする。
       * - | (3)
         - | パスワード再発行用の認証情報が有効である期間の長さを秒単位で指定する。プロパティファイルに定義された値をインジェクションしている。
       * - | (4)
         - | 秘密情報として用いるために、Passayのパスワード生成機能を用いて、パスワード生成規則に従った、長さ10のランダムな文字列を生成する。
       * - | (5)
         - | パスワード再発行用の認証情報に含まれるユーザ名のアカウント情報を取得する。該当するアカウントが存在しない場合、\ ``org.terasoluna.gfw.common.exception.ResourceNotFoundException`` \がthrowされる。
       * - | (6)
         - | トークンとして用いるために、\ ``java.util.UUID`` \クラスの\ ``randomUUID`` \メソッドを用いてランダムな文字列を生成する。
       * - | (7)
         - | 現在時刻に(3)の値を加えることにより、パスワード再発行用の認証情報の有効期限を計算する。
       * - | (8)
         - | パスワード再発行用の認証情報を作成し、ユーザ名、トークン、秘密情報、有効期限を設定する。
       * - | (9)
         - | 秘密情報はハッシュ化を行ってから\ ``PasswordReissueInfo`` \に設定する。
       * - | (10)
         - | パスワード再発行用の認証情報をデータベースに登録する。

  * Formの実装

    .. code-block:: java

       package org.terasoluna.securelogin.app.passwordreissue;

       // omitted

       @Data
       public class CreateReissueInfoForm {
           @NotEmpty
           String username;
       }

  * Viewの実装

    **パスワード再発行のための認証情報生成画面(createReissueInfoForm.xml)**

    .. code-block:: jsp

       <!-- omitted -->

       <body>
           <div id="wrapper">
               <h1>Reissue password</h1>
               <t:messagesPanel />
               <form:form
                   action="${f:h(pageContext.request.contextPath)}/reissue/create"
                   method="Post" modelAttribute="createReissueInfoForm">
                   <table>
                       <tr>
                           <th><form:label path="username" cssErrorClass="error-label">Username</form:label>
                           </th>
                           <td><form:input path="username" cssErrorClass="error-input" /></td>
                           <td><form:errors path="username" cssClass="error-messages" /></td>
                       </tr>
                   </table>

                   <input id="submit" type="submit" value="Reissue password" />
               </form:form>
           </div>
       </body>

       <!-- omitted -->

  * Controllerの実装

    .. code-block:: java

       package org.terasoluna.securelogin.app.passwordreissue;

       // omitted

       @Controller
       @RequestMapping("/reissue")
       public class PasswordReissueController {

       	@Inject
       	PasswordReissueService passwordReissueService;

       	@RequestMapping(value = "create", params = "form")
       	public String showCreateReissueInfoForm(CreateReissueInfoForm form) {
       		return "passwordreissue/createReissueInfoForm";
       	}

       	@RequestMapping(value = "create", method = RequestMethod.POST)
       	public String createReissueInfo(@Validated CreateReissueInfoForm form,
       			BindingResult bindingResult, Model model,
       			RedirectAttributes attributes) {
       		if (bindingResult.hasErrors()) {
       			return showCreateReissueInfoForm(form);
       		}

       		String rawSecret = passwordReissueService.createRawSecret(); // (1)

       		try {
       			passwordReissueService.saveAndSendReissueInfo(form.getUsername(),
       					rawSecret); // (2)
       			attributes.addFlashAttribute("secret", rawSecret);
       			return "redirect:/reissue/create?complete";
       		} catch (ResourceNotFoundException e) {
       			model.addAttribute(e.getResultMessages());
       			return showCreateReissueInfoForm(form);
       		}
       	}

       	@RequestMapping(value = "create", params = "complete", method = RequestMethod.GET)
       	public String createReissueInfoComplete() {
       		return "passwordreissue/createReissueInfoComplete";
       	}

           // omitted

       }
    

    .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
    .. list-table::
       :header-rows: 1
       :widths: 10 90
    
       * - 項番
         - 説明
       * - | (1)
         - | 秘密情報を生成する処理を呼び出す。
       * - | (2)
         - | Formから取得したユーザ名と生成した秘密情報をServiceのメソッドに渡す。パスワード再発行のための認証情報が生成され、データベースに登録される。

* パスワード再発行のための認証情報の有効期限の検査

  パスワード再発行画面にアクセスされた際に、リクエストパラメータとしてURLに含まれるトークンからパスワード再発行のための認証情報を取得し、有効期限内であるかどうかを検査する処理の実装を以下に示す。

  * Serviceの実装

    .. code-block:: java

       package org.terasoluna.securelogin.domain.service.passwordreissue;

       // omitted

       @Service
       @Transactional
       public class PasswordReissueServiceImpl implements PasswordReissueService {

           @Inject
           PasswordReissueInfoRepository passwordReissueInfoRepository;

           // omitted

           @Override
           @Transactional(readOnly = true)
           public PasswordReissueInfo findOne(String username, String token) {
               PasswordReissueInfo info = passwordReissueInfoRepository.findOne(token); // (1)

               if (info == null) {
                   throw new ResourceNotFoundException(ResultMessages.error().add(
                           MessageKeys.E_SL_PR_5002, token));
               }
               if (!info.getUsername().equals(username)) {
                   throw new BusinessException(ResultMessages.error().add(
                           MessageKeys.E_SL_PR_5001));
               }

               if (info.getExpiryDate().isBefore(LocalDateTime.now())) { // (2)
                   throw new BusinessException(ResultMessages.error().add(
                           MessageKeys.E_SL_PR_2001));
               }

               return info;
           }

           // omitted

       }

    .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
    .. list-table::
       :header-rows: 1
       :widths: 10 90
    
       * - 項番
         - 説明
       * - | (1)
         - | 引数として与えられたトークンをキーとして、パスワード再発行のための認証情報をデータベースから取得する。
       * - | (2)
         - | データベースから取得した認証情報に含まれる有効期限が現在時刻よりも前である場合は、\ ``org.terasoluna.gfw.common.exception.BusinessException`` \をthrowする。

  * Controllerの実装

    .. code-block:: java

       package org.terasoluna.securelogin.app.passwordreissue;

       // omitted

       @Controller
       @RequestMapping("/reissue")
       public class PasswordReissueController {

           @Inject
           PasswordReissueService passwordReissueService;

           // omitted

           @RequestMapping(value = "resetpassword", params = "form")
           public String showPasswordResetForm(PasswordResetForm form, Model model,
                   @RequestParam("username") String username, // (1)
                   @RequestParam("token") String token) {  // (2)

               passwordReissueService.findOne(username, token); // (3)

               form.setUsername(username);
               form.setToken(token);
               model.addAttribute("passwordResetForm", form);
               return "passwordreissue/passwordResetForm";
           }

           // omitted

       }

    .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
    .. list-table::
       :header-rows: 1
       :widths: 10 90
    
       * - 項番
         - 説明
       * - | (1)
         - | パスワード再発行画面のURLにリクエストパラメータとして含まれるユーザ名を取得する。
       * - | (2)
         - | パスワード再発行画面のURLにリクエストパラメータとして含まれるトークンを取得する。
       * - | (3)
         - | Serviceのメソッドにユーザ名とトークンを渡して呼び出す。データベースから認証情報が取得され、有効期限が検査される。

* パスワード再発行のための認証情報を用いたユーザの確認

  パスワード再発行画面においてユーザが入力した秘密情報と、パスワード再発行画面のURLに含まれるユーザ名、トークンの組が正しいかどうかを確認する処理の実装を以下に示す。

  * Serviceの実装

    .. code-block:: java

       package org.terasoluna.securelogin.domain.service.passwordreissue;

       // omitted

       public interface PasswordReissueService {

           // omitted

           boolean resetPassword(String username, String token, String secret, // (1)
                   String rawPassword);

           // omitted

       }

    .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
    .. list-table::
       :header-rows: 1
       :widths: 10 90
    
       * - 項番
         - 説明
       * - | (1)
         - | 引数として与えられたユーザ名、トークン、秘密情報を用いてユーザの確認を行った後、新しいパスワードを設定するメソッド


    .. code-block:: java
                    
       package org.terasoluna.securelogin.domain.service.passwordreissue;

       // omitted

       @Service
       @Transactional
       public class PasswordReissueServiceImpl implements PasswordReissueService {

           @Inject
           PasswordReissueInfoRepository passwordReissueInfoRepository;

           @Inject
           AccountSharedService accountSharedService;

           @Inject
           PasswordEncoder passwordEncoder;

           // omitted

           @Override
           public boolean resetPassword(String username, String token, String secret,
                   String rawPassword) {
               PasswordReissueInfo info = this.findOne(username, token); // (1)
               if (!passwordEncoder.matches(secret, info.getSecret())) { // (2)

                   // omitted

                   throw new BusinessException(ResultMessages.error().add(
                       MessageKeys.E_SL_PR_5003));
               }
               passwordReissueInfoRepository.delete(token); // (3)

               //omitted

               return accountSharedService.updatePassword(username, rawPassword); // (4)

           }

           // omitted

       }

    .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
    .. list-table::
       :header-rows: 1
       :widths: 10 90
    
       * - 項番
         - 説明
       * - | (1)
         - | 引数として与えられたユーザ名とトークンを用いて、データベースからパスワード再発行用の認証情報を取得する。このとき、有効期限が改めて検査される。
       * - | (2)
         - | パスワード再発行用の認証情報に含まれるハッシュ化された秘密情報と、引数として与えられた秘密情報を比較する。異なる場合には\ ``BusinessException`` \をthrowする。この場合、パスワードの再発行は失敗となる。
       * - | (3)
         - | 使用された認証情報を再使用不能にするために、データベースから消去する。
       * - | (4)
         - | 引数として渡されたユーザ名を持つアカウントのパスワードを、指定された新しいパスワードに更新する。

  * Formの実装

    .. code-block:: java

       package org.terasoluna.securelogin.app.passwordreissue;

       // omitted

       @Data
       @Compare(source = "newPasssword", destination = "confirmNewPassword", operator = Compare.Operator.EQUAL)
       @StrongPassword(idPropertyName = "username", newPasswordPropertyName = "newPassword") // (1)
       @NotReused(idPropertyName = "username", newPasswordPropertyName = "newPassword") // (2)
       public class PasswordResetForm {

           private String username;

           private String token;

           private String secret;

           private String newPassword;

           private String confirmNewPassword;
       }

    .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
    .. list-table::
       :header-rows: 1
       :widths: 10 90
    
       * - 項番
         - 説明
       * - | (1)
         - | パスワードの強度を検査するためのアノテーション。詳細は :ref:`パスワードの品質チェック <password-strength>` を参照。
       * - | (2)
         - | パスワードの再利用を検査するためのアノテーション。詳細は :ref:`パスワードの品質チェック <password-strength>` を参照。

  * Viewの実装

    **パスワード再発行画面(passwordResetForm.jsp)**

    .. code-block:: jsp

       <body>
           <div id="wrapper">
               <h1>Reset Password</h1>
               <t:messagesPanel />
               <form:form
                   action="${f:h(pageContext.request.contextPath)}/reissue/resetpassword"
                   method="POST" modelAttribute="passwordResetForm">
                   <table>
                       <tr>
                           <th><form:label path="username">Username</form:label></th>
                           <td>${f:h(passwordResetForm.username)} <form:hidden
                                   path="username" value="${f:h(passwordResetorm.username)}" />  <!-- (1) -->
                           </td>
                           <td></td>
                       </tr>
                       <form:hidden path="token" value="${f:h(passwordResetForm.token)}" /> <!-- (2) -->
                       <tr>
                           <th><form:label path="secret" cssErrorClass="error-label">Secret</form:label>
                           </th>
                           <td><form:password path="secret" cssErrorClass="error-input" /></td> <!-- (3) -->
                           <td><form:errors path="secret" cssClass="error-messages" /></td>
                       </tr>
                       <tr>
                           <th><form:label path="newPassword" cssErrorClass="error-label">New password</form:label>
                           </th>
                           <td><form:password path="newPassword"
                                   cssErrorClass="error-input" /></td>
                           <td><form:errors path="newPassword" cssClass="error-messages"
                                   htmlEscape="false" /></td>
                       </tr>
                       <tr>
                           <th><form:label path="confirmNewPassword"
                                   cssErrorClass="error-label">New password(Confirm)</form:label></th>
                           <td><form:password path="confirmNewPassword"
                                   cssErrorClass="error-input" /></td>
                           <td><form:errors path="confirmNewPassword"
                                   cssClass="error-messages" /></td>
                       </tr>
                   </table>

                   <input id="submit" type="submit" value="Reset password" />
               </form:form>
           </div>
       </body>

    .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
    .. list-table::
       :header-rows: 1
       :widths: 10 90
    
       * - 項番
         - 説明
       * - | (1)
         - | ユーザ名をhidden項目として保持する。
       * - | (2)
         - | トークンをhidden項目として保持する。
       * - | (3)
         - | ユーザの確認のために、秘密情報を入力させる。

    **パスワード再発行画面(passwordResetComplete.jsp)**

    .. code-block:: jsp

       <body>
           <div id="wrapper">
               <h1>Your password was successfully reset.</h1>
               <a href="${f:h(pageContext.request.contextPath)}/">go to Top</a>
           </div>
       </body>

  * Controllerの実装

    .. code-block:: java

       package org.terasoluna.securelogin.app.passwordreissue;

       // omitted

       @Controller
       @RequestMapping("/reissue")
       public class PasswordReissueController {

           @Inject
           PasswordReissueService passwordReissueService;

           // omitted

           @RequestMapping(value = "resetpassword", method = RequestMethod.POST)
           public String resetPassword(@Validated PasswordResetForm form,
                   BindingResult bindingResult, Model model) {
               if (bindingResult.hasErrors()) {
                   return showPasswordResetForm(form, model, form.getUsername(),
                           form.getToken());
               }

               try {
                   passwordReissueService.resetPassword(form.getUsername(),
                           form.getToken(), form.getSecret(), form.getNewPassword()); // (1)
                   return "redirect:/reissue/resetpassword?complete";
               } catch (BusinessException e) {
                   model.addAttribute(e.getResultMessages());
                   return showPasswordResetForm(form, model, form.getUsername(),
                           form.getToken());
               }
           }

           @RequestMapping(value = "resetpassword", params = "complete", method = RequestMethod.GET)
           public String resetPasswordComplete() {
               return "passwordreissue/passwordResetComplete";
           }

           // omitted

       }

    .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
    .. list-table::
       :header-rows: 1
       :widths: 10 90
    
       * - 項番
         - 説明
       * - | (1)
         - | Serviceのメソッドにユーザ名、トークン、秘密情報、新しいパスワードを渡す。ユーザ名、トークン、秘密情報の組み合わせが正しい場合、新しいパスワードに更新される。

.. _reissue-info-delivery:

パスワード再発行のための認証情報の配布
--------------------------------------------------------------------------------
実装する要件一覧
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
* :ref:`パスワード再発行画面のURLと秘密情報の別配布 <sec-requirements>`
* :ref:`パスワード再発行画面のURLのメール送付 <sec-requirements>`
  
動作イメージ
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

.. figure:: ./images/SecureLogin_password_reissue.png
   :alt: Page Transition
   :width: 80%
   :align: center

実装方針
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
| :ref:`パスワード再発行のための認証情報の生成 <reissue-info-create>` で生成した認証情報を二つに分け、それぞれ別の方法でユーザに配布する。
| 以下の二つの処理を実装して用いることで要件を実現する。

* 秘密情報の画面表示

  :ref:`パスワード再発行のための認証情報の生成 <reissue-info-create>` で生成したハッシュ化前の秘密情報を、画面に表示させることでユーザに配布する。

* パスワード再発行画面のURLのメール送付

  :ref:`パスワード再発行のための認証情報の生成 <reissue-info-create>` で生成したトークンを含むパスワード再発行画面のURLを、Spring FrameworkのMail連携用コンポーネントを用いて、メールで送付する。
  

コード解説
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

上記の実装方針に従って実装されたコードについて順に解説する。

* 秘密情報の画面表示

  Controllerから秘密情報の生成処理を呼び出し、Viewに表示するための一連の実装を以下に示す。

  .. code-block:: java

     package org.terasoluna.securelogin.app.passwordreissue;

     // omitted

     @Controller
     @RequestMapping("/reissue")
     public class PasswordReissueController {

         @Inject
         PasswordReissueService passwordReissueService;

         // omitted

         @RequestMapping(value = "create", method = RequestMethod.POST)
         public String createReissueInfo(@Validated CreateReissueInfoForm form,
                 BindingResult bindingResult, Model model,
                 RedirectAttributes attributes) {
             if (bindingResult.hasErrors()) {
                 return showCreateReissueInfoForm(form);
             }

             String rawSecret = passwordReissueService.createRawSecret(); // (1)

             try {
                 passwordReissueService.saveAndSendReissueInfo(form.getUsername(),
                         rawSecret);
                 attributes.addFlashAttribute("secret", rawSecret); // (2)
                 return "redirect:/reissue/create?complete"; // (3)
             } catch (ResourceNotFoundException e) {
                 model.addAttribute(e.getResultMessages());
                 return showCreateReissueInfoForm(form);
             }
         }

         @RequestMapping(value = "create", params = "complete", method = RequestMethod.GET)
         public String createReissueInfoComplete() {
             return "passwordreissue/createReissueInfoComplete";
         }

         // omitted

     }

  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
  .. list-table::
     :header-rows: 1
     :widths: 10 90
  
     * - 項番
       - 説明
     * - | (1)
       - | 秘密情報を生成する処理を呼び出す。
     * - | (2)
       - | RedirectAttributesを利用して、リダイレクト先に秘密情報を渡す。
     * - | (3)
       - | パスワード再発行用の認証情報完了画面にリダイレクトする。


  **パスワード再発行用の認証情報生成完了画面(createReissueInfoComplete.jsp)**

  .. code-block:: jsp

     <!-- omitted -->

     <body>
         <div id="wrapper">
             <h1>Your Password Reissue URL was successfully generated.</h1>
             The URL was sent to your registered E-mail address.<br /> Please
             access the URL and enter the secret shown below.
             <h3>Secret : <span id=secret>${f:h(secret)}</span></h3> <!-- (1) -->
         </div>
     </body>

     <!-- omitted -->

  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
  .. list-table::
     :header-rows: 1
     :widths: 10 90
  
     * - 項番
       - 説明
     * - | (1)
       - | 秘密情報を画面に表示する。

* パスワード再発行画面のURLのメール送付

  パスワード再発行用の認証情報からパスワード再発行画面のURLを作成し、メール送付する処理の実装を以下に示す。
  依存ライブラリの追加方法やメールセッションの取得方法等の詳細については、:doc:`../ArchitectureInDetail/Email`を参照。

  .. code-block:: java

     package org.terasoluna.securelogin.domain.service.mail;

     // omitted

     @Service
     public class MailSharedServiceImpl implements MailSharedService {

         @Inject
         JavaMailSender mailSender; // (1)

         @Inject
         SimpleMailMessage templateMessage; // (2)

         // omitted

         @Override
         public void send(String to, String text) {
             SimpleMailMessage message = new SimpleMailMessage(templateMessage); // (3)
             message.setTo(to);
             message.setText(text);
             mailSender.send(message);
         }

     }

  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
  .. list-table::
     :header-rows: 1
     :widths: 10 90
  
     * - 項番
       - 説明
     * - | (1)
       - | \ ``org.springframework.mail.javamail.JavaMailSender`` \のBeanをインジェクションする。
     * - | (2)
       - | 送信元のメールアドレスとメールタイトルが設定された、\ ``org.springframework.mail.SimpleMailMessage`` \のBeanをインジェクションする。
     * - | (3)
       - | テンプレートから\ ``SimpleMailMessage`` \ のインスタンスを生成し、引数として与えられた宛先メールアドレスと本文を設定して送信する。

  .. code-block:: java

     package org.terasoluna.securelogin.domain.service.passwordreissue;

     // omitted

     @Service
     @Transactional
     public class PasswordReissueServiceImpl implements PasswordReissueService {

         @Inject
         MailSharedService mailSharedService;

         @Inject
         AccountSharedService accountSharedService;

         @Inject
         PasswordEncoder passwordEncoder;

         @Value("${security.tokenLifeTime}")
         int tokenLifeTime;

         @Value("${app.hostAndPort}") // (1)
         String hostAndPort;

         @Value("${app.contextPath}")
         String contextPath;

         @Value("${app.passwordReissueProtocol}")
         String protocol;

         // omitted

         @Override
         public boolean saveAndSendReissueInfo(String username, String rowSecret) {
             Account account = accountSharedService.findOne(username);
             
             String token = UUID.randomUUID().toString();

             LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(
                     tokenLifeTime);

             PasswordReissueInfo info = new PasswordReissueInfo();
             info.setUsername(username);
             info.setToken(token);
             info.setSecret(passwordEncoder.encode(rowSecret));
             info.setExpiryDate(expiryDate);
             
             int count = passwordReissueInfoRepository.insert(info);

             if (count > 0) {
                 String passwordResetUrl = protocol + "://" + hostAndPort
                         + contextPath + "/reissue/resetpassword/?form&username="
                         + info.getUsername() + "&token=" + info.getToken(); // (2)

                 mailSharedService.send(account.getEmail(), passwordResetUrl); // (3)

                 return true;
             } else {
                 return false;
             }
         }

         // omitted

     }

  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
  .. list-table::
     :header-rows: 1
     :widths: 10 90
  
     * - 項番
       - 説明
     * - | (1)
       - | パスワード再発行画面のURLに使用するプロトコル、ホスト名、ポート番号、コンテキストパスをプロパティファイルから取得する。
     * - | (2)
       - | (1)で取得した値と、生成したパスワード再発行用の認証情報に含まれるユーザ名、トークンを使用して、ユーザに配布するパスワード再発行画面のURLを作成する。
     * - | (3)
       - | ユーザの登録メールアドレス宛てに、パスワード再発行画面のURLを本文に記したメールを送付する。

.. _reissue-info-invalidate:

パスワード再発行の失敗上限回数の設定
--------------------------------------------------------------------------------
実装する要件一覧
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
* :ref:`パスワード再発行の失敗上限回数の設定 <sec-requirements>`

動作イメージ
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

.. figure:: ./images/SecureLogin_invalidate_token.png
   :alt: Page Transition
   :width: 80%
   :align: center

実装方針
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
| 本実装例では、パスワード再発行に失敗した履歴を「パスワード再発行失敗イベント」エンティティとしてデータベースに保存し、このパスワード再発行失敗イベントエンティティを用いて、パスワード再発行の失敗回数をカウントする。
| 失敗回数があらかじめ設定した上限に達した時点で、パスワード再発行用の認証情報をデータベースから削除し、無効化する。
| 具体的には、以下の二つの処理を実装して用いることにより、要件を実現する。

* パスワード再発行失敗イベントエンティティの保存

  :ref:`パスワード再発行のための認証情報の生成 <reissue-info-create>` における、「パスワード再発行のための認証情報を用いたユーザの確認」処理の中で、ユーザの確認に失敗した場合に、使用したトークンと失敗日時の組をパスワード再発行失敗イベントエンティティとしてデータベースに登録する。

* パスワード再発行用の認証情報の削除

  上記「パスワード再発行失敗イベントエンティティの保存」処理の直後に、同じトークンを持つパスワード再発行失敗イベントエンティティをデータベースから検索して取得する。
  取得した個数があらかじめ設定しておいた失敗回数の上限以上であれば、同じトークンを持つパスワード再発行用の認証情報をデータベースから削除する。

.. warning ::

   | パスワード再発行失敗イベントエンティティはパスワード再発行の失敗回数のカウントのみを目的としているため、不要になったタイミングで消去される。
   | パスワード再発行の失敗時のログが必要な場合は必ず別途ログを保存しておくこと。

コード解説
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

* 共通部分

  前提として、:ref:`パスワード再発行のための認証情報の生成 <reissue-info-create>` に記した各処理が実装されているものとする。
  その他に共通的に必要な、データベースに対するパスワード再発行失敗イベントエンティティの登録、検索、削除に関する実装を以下に示す。

  * Entityの実装

    パスワード再発行失敗イベントエンティティの実装の実装は以下の通り。

    .. code-block:: java

       package org.terasoluna.securelogin.domain.model;

       // omitted

       @Data
       public class FailedPasswordReissue {

           private String token; // (1)

           private LocalDateTime attemptDate; // (2)

       }

    .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
    .. list-table::
       :header-rows: 1
       :widths: 10 90
    
       * - 項番
         - 説明
       * - | (1)
         - | パスワード再発行に使用したトークン
       * - | (2)
         - | パスワード再発行を試行した日時

  * Repositoryの実装

    Entityの検索、登録、削除を行うためのRepositoryを以下に示す。

    .. code-block:: java

       package org.terasoluna.securelogin.domain.repository.passwordreissue;

       // omitted

       public interface FailedPasswordReissueRepository {

           int countByToken(@Param("token") String token); // (1)

           int insert(FailedPasswordReissue event); // (2)

           int deleteByToken(@Param("token") String token); // (3)

           // omitted

       }

    .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
    .. list-table::
       :header-rows: 1
       :widths: 10 90
    
       * - 項番
         - 説明
       * - | (1)
         - | 引数として与えられたトークンをキーとして\ ``FailedPasswordReissue``\ オブジェクトの個数を取得するメソッド
       * - | (2)
         - | 引数として与えられた\ ``FailedPasswordReissue``\ オブジェクトをデータベースのレコードとして登録するメソッド
       * - | (3)
         - | 引数として与えられたトークンをキーとして\ ``FailedPasswordReissue``\ オブジェクトを削除するメソッド

    マッピングファイルは以下の通り。

    .. code-block:: xml

       <?xml version="1.0" encoding="UTF-8"?>
       <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
       "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

       <mapper
       	namespace="org.terasoluna.securelogin.domain.repository.passwordreissue.FailedPasswordReissueRepository">

       	<select id="countByToken" resultType="_int">
           <![CDATA[
               SELECT
                   COUNT(*)
               FROM
                   failed_password_reissue
               WHERE
                   token = #{token}
           ]]>
       	</select>

       	<insert id="insert" parameterType="FailedPasswordReissue">
           <![CDATA[
               INSERT INTO failed_password_reissue (
                   token,
                   attempt_date
               ) VALUES (
       	        #{token},
                   #{attemptDate}
               )
           ]]>
       	</insert>

       	<delete id="deleteByToken">
           <![CDATA[
           	DELETE FROM
           		failed_password_reissue
           	WHERE
           		token = #{token}
           ]]>
       	</delete>

       	<delete id="deleteExpired">
           <![CDATA[
           	DELETE FROM
           		failed_password_reissue
           	WHERE
           		token = 
           		(SELECT
           			token
           		 FROM
           		 	password_reissue_info
           		 WHERE
           		 	token = failed_password_reissue.token 
           		 AND
           			expiry_date < #{date}
           		)
           ]]>
       	</delete>
       </mapper>

以下、実装方針に従って実装されたコードについて順に解説する。

* パスワード再発行失敗イベントエンティティの保存

  パスワード再発行失敗時に行う処理を実装したクラスを以下に示す。
  
  .. code-block:: java

     package org.terasoluna.securelogin.domain.service.passwordreissue;

     public interface PasswordReissueFailureSharedService {

         void resetFailure(String username, String token);

     }

  .. code-block:: java

     package org.terasoluna.securelogin.domain.service.passwordreissue;

     // omitted

     @Service
     @Transactional
     public class PasswordReissueFailureSharedServiceImpl implements
             PasswordReissueFailureSharedService {

         @Inject
         FailedPasswordReissueRepository failedPasswordReissueRepository;

         // omitted

         @Transactional(propagation = Propagation.REQUIRES_NEW) // (1)
         @Override
         public void resetFailure(String username, String token) {
             FailedPasswordReissue event = new FailedPasswordReissue(); // (2)
             event.setToken(token);
             event.setAttemptDate(LocalDateTime.now());
             failedPasswordReissueRepository.insert(event); // (3)

             // omitted

         }

     }

  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
  .. list-table::
     :header-rows: 1
     :widths: 10 90
  
     * - 項番
       - 説明
     * - | (1)
       - | 呼び出し元のServiceとは別にトランザクション管理を行うために、伝搬方法を「REQUIRES_NEW」に指定する。
     * - | (2)
       - | パスワード再発行失敗イベントエンティティを作成し、トークンと失敗日時を設定する。
     * - | (3)
       - | (2)で作成したパスワード再発行失敗イベントエンティティをデータベースに登録する。

  :ref:`パスワード再発行のための認証情報の生成 <reissue-info-create>` の「パスワード再発行のための認証情報を用いたユーザの確認」処理の中から、パスワード再発行失敗時の処理を呼び出す。

  .. code-block:: java

     package org.terasoluna.securelogin.domain.service.passwordreissue;

     // omitted

     @Service
     @Transactional
     public class PasswordReissueServiceImpl implements PasswordReissueService {

         @Inject
         PasswordReissueFailureSharedService passwordReissueFailureSharedService;

         @Inject
         PasswordReissueInfoRepository passwordReissueInfoRepository;

         @Inject
         AccountSharedService accountSharedService;

         @Inject
         PasswordEncoder passwordEncoder;

         // omitted

         @Override
         public boolean resetPassword(String username, String token, String secret,
                 String rawPassword) {
             PasswordReissueInfo info = this.findOne(username, token); // (1)
             if (!passwordEncoder.matches(secret, info.getSecret())) { // (2)
                 passwordReissueFailureSharedService.resetFailure(username, token); // (3)
                 throw new BusinessException(ResultMessages.error().add(  // (4)
                     MessageKeys.E_SL_PR_5003));
             }

             //omitted

         }

         // omitted

     }

  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
  .. list-table::
     :header-rows: 1
     :widths: 10 90
  
     * - 項番
       - 説明
     * - | (1)
       - | 引数として与えられたユーザ名とトークンを用いて、データベースからパスワード再発行用の認証情報を取得する。
     * - | (2)
       - | パスワード再発行用の認証情報に含まれるハッシュ化された秘密情報と、引数として与えられた秘密情報を比較する。
     * - | (3)
       - | パスワード再発行失敗時の処理を行うSharedServiceのメソッド呼び出す。
     * - | (4)
       - | 実行時例外をthrowするが、パスワード再発行失敗時の処理は別のトランザクションで実行されるため、影響を与えることはない。

* パスワード再発行用の認証情報の削除

  パスワード再発行の失敗回数の取得と、失敗回数が上限に達した際の処理の実装を以下に示す。

  .. code-block:: java

     package org.terasoluna.securelogin.domain.service.passwordreissue;

     // omitted

     @Service
     @Transactional
     public class PasswordReissueFailureSharedServiceImpl implements
             PasswordReissueFailureSharedService {

         @Inject
         FailedPasswordReissueRepository failedPasswordReissueRepository;

         @Inject
         PasswordReissueInfoRepository passwordReissueInfoRepository;

         @Value("${security.tokenValidityThreshold}")
         int tokenValidityThreshold; // (1)

         @Transactional(propagation = Propagation.REQUIRES_NEW)
         @Override
         public void resetFailure(String username, String token) {

             // omitted

             int count = failedPasswordReissueRepository // (2)
                     .countByToken(token);
             if (count >= tokenValidityThreshold) { // (3)
                 passwordReissueInfoRepository.delete(token); // (4)
                 failedPasswordReissueRepository.deleteByToken(token); // (5)
             }
         }

     }

  .. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
  .. list-table::
     :header-rows: 1
     :widths: 10 90
  
     * - 項番
       - 説明
     * - | (1)
       - | パスワード再発行の失敗回数の上限値をプロパティファイルから取得して設定する。
     * - | (2)
       - | 引数として与えられたトークンをキーとして、データベースからパスワード再発行失敗イベントエンティティの数を取得。
     * - | (3)
       - | 取得したパスワード再発行の失敗イベントエンティティの数と失敗回数の上限値を比較する。
     * - | (4)
       - | 引数として与えられたトークンをキーとして、パスワード再発行のための認証情報を削除する。
     * - | (5)
       - | パスワード再発行のための認証情報が削除されることで、削除された認証情報と同じトークンを持つパスワード再発行失敗イベントエンティティは不要となるため、削除する。

おわりに
================================================================================

| 本章では、サンプルアプリケーションを題材としてセキュリティ対策の実装方法の例を説明した。
| 実際の開発においては、本実装例における実装方針をそのまま利用できないケースも考えられるため、本章の内容を参考にしつつ要件に合わせてカスタマイズしたり別の方針を考えるようにしてほしい。

Appendix
================================================================================

.. _passay_overview:

Passay
--------------------------------------------------------------------------------

Passayはパスワード入力チェック機能とパスワード生成機能を提供するライブラリである。
PassayのAPIは以下の三つの主要コンポーネントで構成される。

* 検証規則

  パスワードが満たすべき条件の定義。パスワードの長さや含まれる文字種別等の一般的によく利用される規則についてはライブラリが提供するクラスを使用して容易に作成することができる。その他、必要な規則を自分で定義することもできる。

* 検証器

  検証規則に基づいて実際にパスワードのチェックを行うコンポーネント。複数の検証規則を一つの検証器に設定することができる。

* 生成器

  与えられた文字種別に関する検証規則に適合するパスワードを生成するコンポーネント。

Passayの機能を使用する場合は、pom.xmlに以下の定義を追加すること。

.. code-block:: xml

   <dependencies>
     <dependency>
         <groupId>org.passay</groupId>
         <artifactId>passay</artifactId>
         <version>1.1.0</version>
     </dependency>
   <dependencies>

.. _password_validation:

パスワード入力チェック
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

Overview
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

| Passayにおけるパスワード入力チェックの流れの概略図を以下に示す。

.. figure:: ./images/SecureLogin_passay.png
   :alt: Password Vaildation
   :width: 60%
   :align: center

#. | 検証規則を設定した検証器を作成する。検証器は\ ``org.passay.PasswordValidator`` \のインスタンスであり、検証規則は\ ``org.passay.Rule`` \インタフェースを実装したクラスのインスタンスである。
#. | \ ``org.passay.PasswordData`` \ のインスタンスに入力チェックの対象に関する情報を設定する。\ ``PasswordData`` \は、パスワード、ユーザ名、\ ``org.passay.PasswordData.Reference`` \のリストをプロパティとして持つ。\ ``Reference`` \のリストは過去に使用したパスワードのリスト等を保持するために用いる。
#. | \ ``PasswordValidator`` \ の \ ``validate`` \ メソッドに、\ ``PasswordData`` \を引数として渡し、パスワード入力チェックを実行する。
#. | \ ``PasswordValidator`` \ の \ ``validate`` \の結果は、\ ``org.passay.RuleResult`` \ として返される。パスワード入力チェックの結果を真理値として得るためには、\ ``RuleResult`` \ の \ ``isValid`` \ メソッドを用いる。
#. | パスワード入力チェックの結果がエラーであった場合、\ ``PasswordValidator`` \ の \ ``getMessages`` \ メソッドに、\ ``RuleResult`` \を引数として渡すことで、エラーメッセージが取得できる。

Passayが提供している検証規則のクラスの一部を以下の表に示す。

.. tabularcolumns:: |p{0.20\linewidth}|p{0.40\linewidth}|p{0.40\linewidth}|
.. list-table::
   :header-rows: 1
   :widths: 20 40 40

   * - クラス名
     - 説明
     - 主なプロパティ
   * - | \ ``LengthRule`` \
     - | パスワード長の最小値、最大値を規定するための検証規則のクラス
     - | minimuxLength : パスワード長の最小値(int)。コンストラクタまたはsetterで設定。
       | maximumLength : パスワード長の最大値(int)。コンストラクタまたはsetterで設定。
   * - | \ ``CharacterRule`` \
     - | パスワードに含まれるべき文字種別と、その文字種別の最低文字数を規定するための検証規則のクラス
     - | characterData : 文字種別(\ ``org.passay.CharacterData`` \)。コンストラクタで設定。
       | numberOfCharacters : 最低文字数(int)。コンストラクタまたはsetterで設定。
   * - | \ ``CharacterCharacteristicsRule`` \
     - | 複数の\ ``CharacterRule`` \のうち、いくつ以上の規則を満たす必要があるかを規定するための検証規則のクラス
     - | rules : 文字種別に関する検証規則のリスト(\ ``List<CharacterRule>`` \)。setterで設定。
       | numberOfCharacteristics : 満たすべき検証規則の数の最小値(int)。setterで設定。
   * - | \ ``HistoryRule`` \
     - | パスワードが以前に使用したパスワードと一致していないことをチェックするための検証規則のクラス
     - | なし
   * - | \ ``UsernameRule`` \
     - | パスワードがユーザ名を含まないことをチェックするための検証規則のクラス
     - | matchBackwards : ユーザ名を逆にした文字列もチェックする(boolean)。コンストラクタまたはsetterで設定。
       | ignoreCase : 大文字、小文字を区別しない(boolean)。コンストラクタまたはsetterで設定。

この他にも、特定の文字を含む/含まないことのチェックや、正規表現によるチェックを行うための検証規則のクラス等が提供されている。
詳細は `<http://www.passay.org/>`_ を参照。

How to use
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

\ ``PasswordValidator`` \のコンストラクタに\ ``org.passay.Rule`` \のインスタンスのリストを渡すことによって、検証器を作成することができる。
検証規則を設定した検証器を以下のようにBeanとして定義しておくことでDIが可能となる。

.. code-block:: xml

   <!-- Password Rules. -->
   <bean id="upperCaseRule" class="org.passay.CharacterRule"> <!-- (1) -->
       <constructor-arg name="data">
           <value type="org.passay.EnglishCharacterData">UpperCase</value> <!-- (2) -->
       </constructor-arg>
       <constructor-arg name="num" value="1" /> <!-- (3) -->
   </bean>
   <bean id="lowerCaseRule" class="org.passay.CharacterRule"> <!-- (4) -->
       <constructor-arg name="data">
           <value type="org.passay.EnglishCharacterData">LowerCase</value>
       </constructor-arg>
       <constructor-arg name="num" value="1" />
   </bean>
   <bean id="digitRule" class="org.passay.CharacterRule"> <!-- (5) -->
       <constructor-arg name="data">
           <value type="org.passay.EnglishCharacterData">Digit</value>
       </constructor-arg>
       <constructor-arg name="num" value="1" />
   </bean>

   <!-- Password Validator. -->
   <bean id="characterPasswordValidator" class="org.passay.PasswordValidator"> <!-- (6) -->
       <constructor-arg name="rules">
           <list value-type="org.passay.Rule">
               <ref bean="upperCaseRule" />
               <ref bean="lowerCaseRule" />
               <ref bean="digitRule" />
           </list>
       </constructor-arg>
   </bean>

.. tabularcolumns:: |p{0.10\linewidth}|p{0.60\linewidth}|
.. list-table::
   :header-rows: 1
   :widths: 10 90

   * - 項番
     - 説明
   * - | (1)
     - | パスワードに含まれるべき文字種別と、その文字種別の最低文字数を規定するための検証規則のBean定義
   * - | (2) 
     - | 文字種別を指定する。ここでは、\ ``org.passay.EnglishCharacterData.UpperCase`` \を渡しているため、半角英大文字に関する検証規則となる。
   * - | (3)
     - | 文字数を指定する。ここでは"1"を渡しているため、半角英大文字を一文字以上含むことをチェックする検証規則となる。
   * - | (4)
     - | (1)-(3)と同様だが、文字種別として\ ``org.passay.EnglishCharacterData.UpperCase`` \を渡しているため、半角英小文字を一文字以上含むことをチェックする検証規則のBean定義となる。
   * - | (5)
     - | (1)-(3)と同様だが、文字種別として\ ``org.passay.EnglishCharacterData.Digit`` \を渡しているため、半角数字を一文字以上含むことをチェックする検証規則のBean定義となる。
   * - | (6)
     - | 検証器のBean定義。コンストラクタに検証規則のリストを渡す。

作成した検証器を使用してパスワード入力チェックを行う。

.. code-block:: java

   @Inject
   PasswordValidator characterPasswordValidator;

   // omitted

   PasswordData pd = new PasswordData(password); // (1)
   RuleResult result = characterPasswordValidator.validate(pd); // (2)
   if (result.isValid()) { // (3)
      logger.info("Password is valid");
   } else {
      logger.error("Invalid password:");
      for (String msg : characterPasswordValidator.getMessages(result)) { // (4)
          logger.error(msg);
      }
   }

.. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
.. list-table::
   :header-rows: 1
   :widths: 10 90

   * - 項番
     - 説明
   * - | (1)
     - | 検証対象のパスワードを \ ``PasswordData`` \ のコンストラクタに渡し、インスタンスを作成する。
   * - | (2) 
     - | \ ``PasswordValidator`` \ の \ ``validate`` \ メソッドに \ ``PasswordData`` \を引数として渡し、パスワード入力チェックを実行する。
   * - | (3)
     - | \ ``RuleResult`` \ の \ ``isValid`` \ メソッドを使用して、パスワード入力チェックの結果を真理値で取得する。
   * - | (4)
     - | \ ``PasswordValidator`` \ の \ ``getMessages`` \ メソッドに \ ``RuleResult`` \を引数として渡し、エラーメッセージを取得する。

.. _password_generation:

パスワード生成
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

Overview
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

| Passayにおけるパスワード生成機能では、パスワードの生成器と生成規則を用いる。生成器は\ ``org.passay.PasswordGenerator`` \のインスタンスであり、生成規則は文字種別に関する検証規則(\ ``org.passay.CharacterRule`` \)のリストである。
| 生成器のメソッドに生成するパスワードの長さと生成規則を引数として与えることで、生成規則を満たしたパスワードが生成される。

How to use
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

生成規則に含まれる、文字種別に関する検証規則の作成方法は、:ref:`password_validation` と同様である。
生成規則と生成器を以下のようにBeanとして定義しておくことでDIが可能となる。

.. code-block:: xml

   <!-- Password Rules. -->
   <bean id="upperCaseRule" class="org.passay.CharacterRule"> <!-- (1) -->
       <constructor-arg name="data">
           <value type="org.passay.EnglishCharacterData">UpperCase</value> <!-- (2) -->
       </constructor-arg>
       <constructor-arg name="num" value="1" /> <!-- (3) -->
   </bean>
   <bean id="lowerCaseRule" class="org.passay.CharacterRule"> <!-- (4) -->
       <constructor-arg name="data">
           <value type="org.passay.EnglishCharacterData">LowerCase</value>
       </constructor-arg>
       <constructor-arg name="num" value="1" />
   </bean>
   <bean id="digitRule" class="org.passay.CharacterRule"> <!-- (5) -->
       <constructor-arg name="data">
           <value type="org.passay.EnglishCharacterData">Digit</value>
       </constructor-arg>
       <constructor-arg name="num" value="1" />
   </bean>

    <!-- Password Generator. -->
    <bean id="passwordGenerator" class="org.passay.PasswordGenerator" /> <!-- (6) -->
    <bean id="passwordGenerationRules"
        class="org.springframework.beans.factory.config.ListFactoryBean">
        <property name="sourceList">
            <list value-type="org.passay.CharacterRule"> <!-- (7) -->
                <ref bean="upperCaseRule" />
                <ref bean="lowerCaseRule" />
                <ref bean="digitRule" />
            </list>
        </property>
    </bean>

.. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
.. list-table::
   :header-rows: 1
   :widths: 10 90

   * - 項番
     - 説明
   * - | (1)
     - | パスワードに含まれるべき文字種別と、その文字種別の最低文字数を規定するための検証規則のBean定義
   * - | (2) 
     - | 文字種別を指定する。ここでは、\ ``org.passay.EnglishCharacterData.UpperCase`` \を渡しているため、半角英大文字に関する検証規則となる。
   * - | (3)
     - | 文字数を指定する。ここでは"1"を渡しているため、半角英大文字を一文字以上含むことをチェックする検証規則となる。
   * - | (4)
     - | (1)-(3)と同様だが、文字種別として\ ``org.passay.EnglishCharacterData.UpperCase`` \を渡しているため、半角英小文字を一文字以上含むことをチェックする検証規則のBean定義となる。
   * - | (5)
     - | (1)-(3)と同様だが、文字種別として\ ``org.passay.EnglishCharacterData.Digit`` \を渡しているため、半角数字を一文字以上含むことをチェックする検証規則のBean定義となる。
   * - | (6)
     - | 生成器のBean定義
   * - | (7)
     - | 生成規則のBean定義。(1)-(5)で定義した、文字種別に関する検証規則のリストとして定義する。

作成した生成器と生成規則を使用してパスワード生成を行う。

.. code-block:: java

   @Inject
   PasswordGenerator passwordGenerator;

   @Resource(name = "passwordGenerationRules")
   List<CharacterRule> passwordGenerationRules;

   // omitted

   String password = passwordGenerator.generatePassword(10, passwordGenerationRules); // (1)

.. tabularcolumns:: |p{0.10\linewidth}|p{0.90\linewidth}|
.. list-table::
   :header-rows: 1
   :widths: 10 90

   * - 項番
     - 説明
   * - | (1)
     - | \ ``PasswordGenerator`` \の\ ``generatePassword`` \メソッドに、生成するパスワードの長さと生成規則を引数として渡すと、生成規則を満たしたパスワードが生成される。

.. raw:: latex

   \newpage

