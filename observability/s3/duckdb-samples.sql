SELECT UNNEST(SELECT resourceSpans FROM read_json_auto('s3://data-demo/topics/otlp_spans/partition*/*'));

SELECT * FROM read_json_auto('s3://data-demo/topics/otlp_spans/*/*'), UNNEST(resourceSpans);

-- select all
SELECT * FROM read_json_auto('s3://data-demo/topics/otlp_spans/*/*');

-- select resource spans
SELECT *
FROM (
    SELECT UNNEST((SELECT ARRAY_AGG(resourceSpans) FROM read_json_auto('s3://data-demo/topics/otlp_spans/partition*/*')), recursive := true)
);

-- select scope spans
SELECT *
FROM (
    SELECT UNNEST((SELECT ARRAY_AGG(scopeSpans) FROM (
        SELECT UNNEST((SELECT ARRAY_AGG(resourceSpans) FROM read_json_auto('s3://data-demo/topics/otlp_spans/partition*/*')), recursive := true)
    )), recursive := true)
);

-- select nested spans
SELECT *
FROM (
    SELECT UNNEST((SELECT ARRAY_AGG(spans) FROM (
        SELECT UNNEST((SELECT ARRAY_AGG(scopeSpans) FROM (
            SELECT UNNEST((SELECT ARRAY_AGG(resourceSpans) FROM read_json_auto('s3://data-demo/topics/otlp_spans/partition*/*')), recursive := true)
        )), recursive := true)
    )), recursive := true)
);

-- select nested messaging spans
SELECT *
FROM (
    SELECT UNNEST((SELECT ARRAY_AGG(spans) FROM (
        SELECT UNNEST((SELECT ARRAY_AGG(scopeSpans) FROM (
            SELECT UNNEST((SELECT ARRAY_AGG(resourceSpans) FROM read_json_auto('s3://data-demo/topics/otlp_spans/partition*/*')), recursive := true)
        )), recursive := true)
    )), recursive := true)
)
WHERE EXISTS (
    SELECT *
    FROM UNNEST(attributes) as attributes
    WHERE attributes.key = 'messaging.system'
);

-- select nested span attributes
SELECT *
FROM (
    SELECT UNNEST((SELECT ARRAY_AGG(attributes) FROM (
        SELECT UNNEST((SELECT ARRAY_AGG(spans) FROM (
            SELECT UNNEST((SELECT ARRAY_AGG(scopeSpans) FROM (
                SELECT UNNEST((SELECT ARRAY_AGG(resourceSpans) FROM read_json_auto('s3://data-demo/topics/otlp_spans/partition*/*')), recursive := true)
            )), recursive := true)
        )), recursive := true)
    )), recursive := true)
);

-- select nested messaging span attributes
SELECT *
FROM (
    SELECT UNNEST((SELECT ARRAY_AGG(attributes) FROM (
        SELECT UNNEST((SELECT ARRAY_AGG(spans) FROM (
            SELECT UNNEST((SELECT ARRAY_AGG(scopeSpans) FROM (
                SELECT UNNEST((SELECT ARRAY_AGG(resourceSpans) FROM read_json_auto('s3://data-demo/topics/otlp_spans/partition*/*')), recursive := true)
            )), recursive := true)
        )), recursive := true)
    )), recursive := true)
)
WHERE key = 'messaging.system';

-- select spans with specific attribute
SELECT traceId, count(traceId) as spanCount
FROM (
    SELECT UNNEST((SELECT ARRAY_AGG(spans) FROM (
        SELECT UNNEST((SELECT ARRAY_AGG(scopeSpans) FROM (
            SELECT UNNEST((SELECT ARRAY_AGG(resourceSpans) FROM read_json_auto('s3://data-demo/topics/otlp_spans/partition*/*')), recursive := true)
        )), recursive := true)
    )), recursive := true)
)
WHERE EXISTS (
    SELECT *
    FROM UNNEST(attributes) as attributes
    WHERE attributes.key = 'messaging.system'
)
GROUP BY traceId;

-- PRODUCER SPANS TO OUTPUT
SELECT traceId, count(traceId) as spanCount
FROM (
    SELECT UNNEST((SELECT ARRAY_AGG(spans) FROM (
        SELECT UNNEST((SELECT ARRAY_AGG(scopeSpans) FROM (
            SELECT UNNEST((SELECT ARRAY_AGG(resourceSpans) FROM read_json_auto('s3://data-demo/topics/otlp_spans/partition*/*')), recursive := true)
        )), recursive := true)
    )), recursive := true)
)
WHERE (kind = 'SPAN_KIND_CONSUMER' OR kind = 'SPAN_KIND_PRODUCER') AND 
    EXISTS (
        SELECT *
        FROM UNNEST(attributes) as attributes
        WHERE (
            attributes.value.stringValue = 'kafka-workshop-top-10-stream-count' 
            OR 
            attributes.value.stringValue = 'data-demo-streams'
        )
    )
GROUP BY traceId;

-- find traces without 2 spans
SELECT * FROM (
    SELECT traceId, count(traceId) as spanCount
    FROM (
        SELECT UNNEST((SELECT ARRAY_AGG(spans) FROM (
            SELECT UNNEST((SELECT ARRAY_AGG(scopeSpans) FROM (
                SELECT UNNEST((SELECT ARRAY_AGG(resourceSpans) FROM read_json_auto('s3://data-demo/topics/otlp_spans/partition*/*')), recursive := true)
            )), recursive := true)
        )), recursive := true)
    )
    WHERE 
        -- consumer and producer spans only
        (kind = 'SPAN_KIND_CONSUMER' OR kind = 'SPAN_KIND_PRODUCER') 
        AND 
        -- consumed from streams and published to top-10
        EXISTS (
            SELECT *
            FROM UNNEST(attributes) as attributes
            WHERE (
                attributes.value.stringValue = 'kafka-workshop-top-10-stream-count' 
                OR 
                attributes.value.stringValue = 'data-demo-streams'
            )
        )
        AND
        -- last 10 minutes
        (AGE(TO_TIMESTAMP(CAST(startTimeUnixNano / 1000000000 AS BIGINT))) < INTERVAL 1 MINUTE)
    GROUP BY traceId
);

SELECT * FROM (
    SELECT traceId, spanId, kind
    FROM (
        SELECT UNNEST((SELECT ARRAY_AGG(spans) FROM (
            SELECT UNNEST((SELECT ARRAY_AGG(scopeSpans) FROM (
                SELECT UNNEST((SELECT ARRAY_AGG(resourceSpans) FROM read_json_auto('s3://data-demo/topics/otlp_spans/partition*/*')), recursive := true)
            )), recursive := true)
        )), recursive := true)
    )
    WHERE 
        -- consumer and producer spans only
        (kind = 'SPAN_KIND_CONSUMER' OR kind = 'SPAN_KIND_PRODUCER') 
        AND 
        -- consumed from streams and published to top-10
        EXISTS (
            SELECT *
            FROM UNNEST(attributes) as attributes
            WHERE (
                attributes.value.stringValue = 'kafka-workshop-top-10-stream-count' 
                OR 
                attributes.value.stringValue = 'data-demo-streams'
            )
        )
        -- AND
        -- last 10 minutes
        -- (AGE(TO_TIMESTAMP(CAST(startTimeUnixNano / 1000000000 AS BIGINT))) < INTERVAL 10 MINUTE)
);
WHERE spanCount < 2;


SELECT
  MAP_FROM_ENTRIES(
    ARRAY_AGG(
      ARRAY[attr.key, attr.value.stringValue]
      FILTER (
        attr.key IS NOT NULL AND attr.value.stringValue IS NOT NULL
      )
    )
  ) AS attribute_map
FROM your_table, UNNEST(attributes) AS attr;
  

CREATE MACRO trace_details(id) AS (
    SELECT * FROM (
        SELECT 
            traceId, 
            spanId, 
            LIST_TRANSFORM(LIST_FILTER(attributes, x -> x.value.stringValue IS NOT NULL), x -> struct_pack(k := x.key, v := x.value.stringValue)) as stringAttr,
            MAP_FROM_ENTRIES(LIST_TRANSFORM(LIST_FILTER(attributes, x -> x.value.intValue IS NOT NULL), x -> struct_pack(k := x.key, v := x.value.intValue))) as intAttr
        FROM (
            SELECT UNNEST((SELECT ARRAY_AGG(spans) FROM (
                SELECT UNNEST((SELECT ARRAY_AGG(scopeSpans) FROM (
                    SELECT UNNEST((SELECT ARRAY_AGG(resourceSpans) FROM 
                        read_json_auto('s3://data-demo/topics/otlp_spans/partition*/*')), recursive := true)
                )), recursive := true)
            )), recursive := true)
        )
        WHERE traceId = id
    )
);

-- select attributes for a specific trace
SELECT * FROM (
    SELECT 
        traceId, 
        spanId, 
        MAP_FROM_ENTRIES(
            LIST_CONCAT(
                LIST_TRANSFORM(LIST_FILTER(attributes, x -> x.value.stringValue IS NOT NULL), x -> struct_pack(k := x.key, v := x.value.stringValue)),
                LIST_TRANSFORM(LIST_FILTER(attributes, x -> x.value.intValue IS NOT NULL), x -> struct_pack(k := x.key, v := x.value.intValue))
            )
        ) as attr
    FROM (
        SELECT UNNEST((SELECT ARRAY_AGG(spans) FROM (
            SELECT UNNEST((SELECT ARRAY_AGG(scopeSpans) FROM (
                SELECT UNNEST((SELECT ARRAY_AGG(resourceSpans) FROM 
                    read_json_auto('s3://data-demo/topics/otlp_spans/partition*/*')), recursive := true)
            )), recursive := true)
        )), recursive := true)
    )
    WHERE traceId = '5938da88-4cd4-c361-f2a6-b9bdaba17adb'
);

SELECT traceId, spanId, attr['messaging.kafka.message.key'] FROM (
    SELECT 
        traceId, 
        spanId, 
        MAP_FROM_ENTRIES(
            LIST_CONCAT(
                LIST_TRANSFORM(LIST_FILTER(attributes, x -> x.value.stringValue IS NOT NULL), x -> struct_pack(k := x.key, v := x.value.stringValue)),
                LIST_TRANSFORM(LIST_FILTER(attributes, x -> x.value.intValue IS NOT NULL), x -> struct_pack(k := x.key, v := x.value.intValue))
            )
        ) as attr
    FROM (
        SELECT UNNEST((SELECT ARRAY_AGG(spans) FROM (
            SELECT UNNEST((SELECT ARRAY_AGG(scopeSpans) FROM (
                SELECT UNNEST((SELECT ARRAY_AGG(resourceSpans) FROM 
                    read_json_auto('s3://data-demo/topics/otlp_spans/partition*/*')), recursive := true)
            )), recursive := true)
        )), recursive := true)
    )
    WHERE traceId = '5938da88-4cd4-c361-f2a6-b9bdaba17adb'
);



COPY (
    SELECT * FROM (
        SELECT 
            traceId, 
            spanId, 
            MAP_FROM_ENTRIES(
                LIST_CONCAT(
                    LIST_TRANSFORM(LIST_FILTER(attributes, x -> x.value.stringValue IS NOT NULL), x -> struct_pack(k := x.key, v := x.value.stringValue)),
                    LIST_TRANSFORM(LIST_FILTER(attributes, x -> x.value.intValue IS NOT NULL), x -> struct_pack(k := x.key, v := x.value.intValue))
                )
            ) as attr
        FROM (
            SELECT UNNEST((SELECT ARRAY_AGG(spans) FROM (
                SELECT UNNEST((SELECT ARRAY_AGG(scopeSpans) FROM (
                    SELECT UNNEST((SELECT ARRAY_AGG(resourceSpans) FROM 
                        read_json_auto('s3://data-demo/topics/otlp_spans/partition*/*')), recursive := true)
                )), recursive := true)
            )), recursive := true)
        )
        WHERE traceId = '5938da88-4cd4-c361-f2a6-b9bdaba17adb'
    )
) TO 'trace-attributes.csv' (HEADER, DELIMITER ',');


-- pivot on span attributes key?
PIVOT (SELECT * FROM (
    SELECT traceId, spanId, UNNEST(attributes, recursive := true)
    FROM (
        SELECT UNNEST((SELECT ARRAY_AGG(spans) FROM (
            SELECT UNNEST((SELECT ARRAY_AGG(scopeSpans) FROM (
                SELECT UNNEST((SELECT ARRAY_AGG(resourceSpans) FROM 
                    read_json_auto('s3://data-demo/topics/otlp_spans/partition*/*')), recursive := true)
            )), recursive := true)
        )), recursive := true)
    )
    WHERE traceId = '5938da88-4cd4-c361-f2a6-b9bdaba17adb'
)) ON key GROUP BY traceId;

WHERE key = 'messaging.destination.name';

-- select attributes for a specific span
SELECT * FROM (
    SELECT traceId, spanId, UNNEST(attributes, recursive := true)
    FROM (
        SELECT UNNEST((SELECT ARRAY_AGG(spans) FROM (
            SELECT UNNEST((SELECT ARRAY_AGG(scopeSpans) FROM (
                SELECT UNNEST((SELECT ARRAY_AGG(resourceSpans) FROM read_json_auto('s3://data-demo/topics/otlp_spans/partition*/*')), recursive := true)
            )), recursive := true)
        )), recursive := true)
    )
    WHERE spanId = '3b351b08edd2fd24'
);


-- SPAN_KIND_CONSUMER
{
  "startTimeUnixNano": "1687882531862890625",
  "spanId": "bb02fa7d035fd839",
  "name": "kafka-workshop-top-10-stream-count send",
  "status": {},
  "kind": "SPAN_KIND_PRODUCER",
  "attributes": [
    {
      "key": "thread.name",
      "value": {
        "stringValue": "kafka-workshop-client-StreamThread-1"
      }
    },
    {
      "key": "messaging.destination.name",
      "value": {
        "stringValue": "kafka-workshop-top-10-stream-count"
      }
    },
    {
      "key": "thread.id",
      "value": {
        "intValue": "20"
      }
    },
    {
      "key": "messaging.kafka.destination.partition",
      "value": {
        "intValue": "0"
      }
    },
    {
      "key": "messaging.kafka.message.offset",
      "value": {
        "intValue": "490"
      }
    },
    {
      "key": "messaging.destination.kind",
      "value": {
        "stringValue": "topic"
      }
    },
    {
      "key": "messaging.kafka.client_id",
      "value": {
        "stringValue": "kafka-workshop-client-StreamThread-1-producer"
      }
    },
    {
      "key": "messaging.system",
      "value": {
        "stringValue": "kafka"
      }
    }
  ],
  "endTimeUnixNano": "1687882537174463333",
  "parentSpanId": "b78bf4be68055590",
  "traceId": "9c526c826e15139ec8de4b4a0a7d4a9c"
}

-- SPAN_KIND_CONSUMER
{
    "startTimeUnixNano": "1687882537202731000",
    "spanId": "9940335474a0da38",
    "name": "kafka-workshop-example-7ebec765-8036-4f19-9145-db971032dc5b-customer-artist-stream-counts-repartition process",
    "status": {},
    "kind": "SPAN_KIND_CONSUMER",
    "attributes": [
    {
        "key": "messaging.message.payload_size_bytes",
        "value": {
        "intValue": "109"
        }
    },
    {
        "key": "thread.id",
        "value": {
        "intValue": "20"
        }
    },
    {
        "key": "messaging.kafka.message.offset",
        "value": {
        "intValue": "3121"
        }
    },
    {
        "key": "messaging.consumer.id",
        "value": {
        "stringValue": "kafka-workshop-example-7ebec765-8036-4f19-9145-db971032dc5b - kafka-workshop-client-StreamThread-1-consumer"
        }
    },
    {
        "key": "messaging.kafka.client_id",
        "value": {
        "stringValue": "kafka-workshop-client-StreamThread-1-consumer"
        }
    },
    {
        "key": "thread.name",
        "value": {
        "stringValue": "kafka-workshop-client-StreamThread-1"
        }
    },
    {
        "key": "messaging.destination.name",
        "value": {
        "stringValue": "kafka-workshop-example-7ebec765-8036-4f19-9145-db971032dc5b-customer-artist-stream-counts-repartition"
        }
    },
    {
        "key": "messaging.operation",
        "value": {
        "stringValue": "process"
        }
    },
    {
        "key": "messaging.kafka.consumer.group",
        "value": {
        "stringValue": "kafka-workshop-example-7ebec765-8036-4f19-9145-db971032dc5b"
        }
    },
    {
        "key": "messaging.destination.kind",
        "value": {
        "stringValue": "topic"
        }
    },
    {
        "key": "messaging.system",
        "value": {
        "stringValue": "kafka"
        }
    },
    {
        "key": "messaging.kafka.source.partition",
        "value": {
        "intValue": "2"
        }
    },
    {
        "key": "messaging.kafka.message.key",
        "value": {
        "stringValue": "367659726"
        }
    }
    ],
    "endTimeUnixNano": "1687882537202765375",
    "parentSpanId": "b05073d06b0ef561",
    "traceId": "11b413ff4341e316392b5d0fa73faa0d"
    }


 {
                  "key": "messaging.kafka.message.key",
                  "value": {
                    "stringValue": "260914568"
                  }
                },
                {
                  "key": "messaging.system",
                  "value": {
                    "stringValue": "kafka"
                  }
                },
                {
                  "key": "messaging.destination.kind",
                  "value": {
                    "stringValue": "topic"
                  }
                },
                {
                  "key": "messaging.kafka.client_id",
                  "value": {
                    "stringValue": "data-demo-producer"
                  }
                },
                {
                  "key": "messaging.kafka.message.offset",
                  "value": {
                    "intValue": "77"
                  }
                }