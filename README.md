# Elasticsearch fingerprint Ingest Processor

Elasticsearch fingerprint Ingest Processor is for generating fingerprint from document content, which is similar to [Logstash Fingerprint filter plugin](https://www.elastic.co/guide/en/logstash/current/plugins-filters-fingerprint.html)

Idea derived from this [issue](https://github.com/elastic/elasticsearch/issues/53578).

## Build
```
./gradlew assemble
```

## Installation

```
bin/elasticsearch-plugin install file:///path/to/your/ingest-fingerprint-X.X.X-SNAPSHOT.zip
```



## Usage



### Parameter

| **Name**                   | **Required** | **Default** | **Description**                                              |
| -------------------------- | ------------ | ----------- | ------------------------------------------------------------ |
| **fields**                 | Yes          | -           | List of fields whose name and value will be used to calc hash value. |
| **target_field**           | No           | fingerprint | The field to assign the hash value to.                       |
| **method**                 | No           | MD5         | The hash methods, one of ["SHA1", "SHA256", "SHA512", "MD5", "MURMUR3" "UUID"] |
| **base64_encode**          | No           | false       | Using base64 encoding for fingerprint.                       |
| **concatenate_all_fields** | No           | false       | When set to true and method isn’t UUID, the plugin concatenates the names and values of all document fields including source and meta fields into one string and uses it to compute fingerprint. |
| **ignore_missing**         | No           | false       | If true and some of "fields" does not exist, the processor won't use them to compute fingerprint. If all is missing, the processor will exit quietly without modifing the document. |

### Example



1. Computing fingerprint from field "field1" using the default method "MD5" and write it to default target field "fingerprint".

```
PUT _ingest/pipeline/my_pipeline
{
    "processors": [
        {
            "fingerprint": {
                "fields": ["field1"]
            }
        }
    ]
}

```



2. Computing fingerprint from the fields "field1" and "field2" using the method "SHA1",  encoding it by BASE64 and write it to target field "field3". Since "ignore_missing" is setted, this plugin will ignore missing fields or just exit without modifing the document when all is missing.

```
PUT _ingest/pipeline/my_pipeline
{
    "processors": [
        {
            "fingerprint": {
                "fields": ["field1", "field2"],
                "method": "SHA1",
                "target_field": "field3",
                "base64_encode": true,
                "ignore_missing": true
            }
        }
    ]
}
```

3. Computing fingerprint from the all document fields including source and meta fields using the method "SHA256",  encoding it by BASE64 and write it to target field "field3"

```
PUT _ingest/pipeline/my_pipeline
{
    "processors": [
        {
            "fingerprint": {
                "method": "SHA256",
                "target_field": "field3",
                "base64_encode": true,
                "concatenate_all_fields": true
            }
        }
    ]
}
```



