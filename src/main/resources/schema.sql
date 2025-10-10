CREATE
    TABLE
        shopping_cart(
            id UUID,
            PRIMARY KEY(id)
        );

CREATE
    TABLE
        item(
            id UUID,
            article_id UUID,
            article_number TEXT,
            title TEXT,
            image_url TEXT,
            quantity SMALLINT,
            price_per_piece MONEY,
            shopping_cart_id UUID,
            quantity_per_unit_value DECIMAL,
            quantity_unit TEXT,
            insertion_time TIMESTAMP,
            PRIMARY KEY(id)
        );

CREATE
    TABLE
        article(
            id UUID,
            article_number TEXT,
            title TEXT,
            description TEXT,
            image_url TEXT,
            price_per_unit MONEY,
            quantity_per_unit_value DECIMAL,
            quantity_unit TEXT,
            PRIMARY KEY(id)
        );
