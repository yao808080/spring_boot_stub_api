# スタブ API 利用ガイド

Spring Boot を基盤にした REST / SOAP のスタブ環境について説明します。同一アプリケーション内で両方のインターフェースを提供します。

## 1. 必要環境
- Java 17
- Maven 3.9 以上

## 2. プロジェクト構成
```
src/
  main/
    java/com/example/stubapi/
      rest/  … REST スタブ関連実装
      soap/  … SOAP スタブ関連実装
    resources/
      application.yml       … REST スタブ定義
      ws/schemas/simple.xsd … SOAP スキーマ定義
```

## 3. アプリケーションの起動
```bash
mvn spring-boot:run
```
デフォルトの待受ポートは `http://localhost:8080` です。

## 4. REST スタブの設定と利用
- エンドポイントには `/stub/rest` プレフィックスが付きます。
- `application.yml` の `stub.rest.responses` にメソッド・パス・ステータス・ヘッダー・ボディを記述します。
- サンプル呼び出し: `GET /stub/rest/customers/123`
  ```bash
  curl http://localhost:8080/stub/rest/customers/123
  ```
- 新規スタブ登録例:
  ```yaml
  stub:
    rest:
      responses:
        - method: PUT
          path: /inventory/ABC
          status: 202
          headers:
            Content-Type: application/json
          body: |
            { "status": "Accepted" }
  ```

## 5. SOAP スタブの設定と利用
- サービス URI: `http://localhost:8080/stub/soap`
- 公開 WSDL: `http://localhost:8080/stub/soap/simple.wsdl`
- リクエスト例:
  ```xml
  <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                    xmlns:stub="http://example.com/stub/soap">
    <soapenv:Header/>
    <soapenv:Body>
      <stub:SimpleRequest>
        <stub:code>ABC-123</stub:code>
      </stub:SimpleRequest>
    </soapenv:Body>
  </soapenv:Envelope>
  ```
- `code` 要素の値がレスポンスメッセージに反映されます。

## 6. テスト方法
```bash
mvn test
```
MockMvc と MockWebServiceClient による統合テストが実行され、REST / SOAP の基本動作を検証します。

## 7. 代表的なカスタマイズ
- **ポート変更**: `application.yml` の `server.port` を書き換えます。
- **REST レスポンスの調整**: YAML のスタブ定義を更新すると再起動のみで反映されます。
- **SOAP 要素の拡張**: `simple.xsd` のスキーマを拡張し、`StubSoapEndpoint` の処理を追加してください。

## 8. トラブルシューティング
- **404 が返る**: `stub.rest.responses` に対応するメソッドとパスが存在するか確認します。
- **SOAP で値が反映されない**: リクエストの名前空間が `http://example.com/stub/soap` になっているか確認します。
- **WSDL が取得できない**: アプリ起動ログにエラーがないか、`wsdl4j` 依存関係が解決されているかを確認します。
