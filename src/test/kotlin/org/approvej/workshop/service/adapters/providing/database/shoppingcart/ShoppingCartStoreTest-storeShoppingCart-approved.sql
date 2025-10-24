select
    sce1_0.id,
    i1_0.shopping_cart_id,
    i1_0.id,
    i1_0.article_id,
    i1_0.article_number,
    i1_0.image_url,
    i1_0.insertion_time,
    i1_0.price_per_piece,
    i1_0.quantity,
    i1_0.quantity_per_unit_value,
    i1_0.quantity_unit,
    i1_0.title
from
    shopping_cart sce1_0
left join
    item i1_0
        on sce1_0.id=i1_0.shopping_cart_id
where
    sce1_0.id=?;

insert
into
    shopping_cart
    (id)
values
    (?);

select
    sce1_0.id,
    i1_0.shopping_cart_id,
    i1_0.id,
    i1_0.article_id,
    i1_0.article_number,
    i1_0.image_url,
    i1_0.insertion_time,
    i1_0.price_per_piece,
    i1_0.quantity,
    i1_0.quantity_per_unit_value,
    i1_0.quantity_unit,
    i1_0.title
from
    shopping_cart sce1_0
left join
    item i1_0
        on sce1_0.id=i1_0.shopping_cart_id
where
    sce1_0.id=?
