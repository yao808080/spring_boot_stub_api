# Spring Boot スタブ API

Spring Boot を用いて REST API と SOAP API をスタブ化するための開発向けサーバーです。外部システムの実装が未完了でも、所定のレスポンスを返す API をすぐに利用できます。

## 必要環境
- Java 17
- Maven 3.9 以上

## 起動方法
```bash
mvn spring-boot:run
```
アプリケーションは既定で `http://localhost:8080` で待ち受けます。

## REST スタブの利用
`application.yml` の `stub.rest.responses` に、パス・HTTP メソッド・レスポンス内容を定義します。初期状態では次の 2 件を登録しています。

- `GET /customers/123`
- `POST /orders`

アクセス時は `/stub/rest` プレフィックスを付与します。

```bash
curl http://localhost:8080/stub/rest/customers/123
```

レスポンスのヘッダーや本文は任意に変更可能です。スタブを追加する場合は `method` / `path` / `status` / `headers` / `body` を追記してください。

```yaml
stub:
  rest:
    responses:
      - method: GET
        path: /products/ABC
        status: 200
        headers:
          Content-Type: application/json
        body: |
          { "sku": "ABC", "name": "Sample product" }
```

## SOAP スタブの利用
SOAP スタブは `/stub/soap` で公開され、WSDL は `http://localhost:8080/stub/soap/simple.wsdl` から取得できます。`SimpleRequest` メッセージの `code` 要素に値を指定すると、レスポンスのメッセージに反映されます。

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

## テスト実行
```bash
mvn test
```
REST・SOAP 双方のエンドポイントに対する基本動作テストが実行されます。

## 追加ドキュメント
より詳しい手順やカスタマイズ方法は `docs/stub-api-guide.md` を参照してください。
