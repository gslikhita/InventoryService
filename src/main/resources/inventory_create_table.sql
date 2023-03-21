CREATE TABLE public.inventory
(
    sku              varchar   NOT NULL,
    id               serial   NOT NULL,
    "type"           varchar   NOT NULL,
    status           varchar NULL DEFAULT 'created':: character varying,
    "location"       varchar   NOT NULL,
    created_at       timestamp NOT NULL,
    updated_at       timestamp NOT NULL,
    created_by       varchar   NOT NULL,
    updated_by       varchar   NOT NULL,
    "attributes"     json      NOT NULL,
    cost_price       float4    NOT NULL,
    sold_at          float4 NULL,
    secondary_status json      NOT NULL,
    CONSTRAINT inventory_pkey PRIMARY KEY (id)
);
