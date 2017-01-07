truncate table users;
truncate table entry;
truncate table entry_modifier;
truncate table client;
truncate table audit;
truncate table size;
truncate table product;
truncate table domain_value;
truncate table location;

drop table users;
create table users (
	username varchar(60),
	password varchar(60),
	user_role varchar(20)
);

drop table entry;
create table entry (
	ID int IDENTITY(1,1) PRIMARY KEY,
	audit_id int not null,
	location varchar(20),
	product_id int not null,
	amount decimal(8,2),
	weight decimal(8,2),
	incoming decimal(8,2),
	open_bottles decimal(8,2),
	bin varchar(40),
	weights varchar(200),
	modifiers varchar(2000),
	object_id varchar(10),
	update_datetime DATETIME not null
);

drop table entry_modifier;
create table entry_modifier (
	ID int IDENTITY(1,1) PRIMARY KEY,
        entry_id int not null,
        name varchar(80),
        fulls int,
        ounces decimal(8,2));

drop table entry_partial_weight;
create table entry_partial_weight (
        entry_id int not null,
		weight decimal(8,2));

drop table client;
create table client (
        name varchar(80) PRIMARY KEY,
        phone varchar(20),
        email varchar(100),
        object_id varchar(10),
        update_datetime DATETIME not null);

drop table audit;
create table audit(
	ID int IDENTITY(1,1) PRIMARY KEY,
	client_name varchar(80) not null,
	audit_date  DATETIME not null,
	notes varchar(200),
	object_id varchar(10),
	update_datetime DATETIME not null);

drop table size;
create table size (
	ID int IDENTITY(1,1) PRIMARY KEY,
	name varchar(20) not null,
	ounces decimal(8,2),
	object_id varchar(10),
	update_datetime DATETIME not null);

drop table product;

create table product(
        ID int IDENTITY(1,1) PRIMARY KEY,
	name varchar(100) not null,
	size_id int not null,
	serving varchar(20),
	tare decimal(8,2),
	upc varchar(60),
	cost decimal(8,2),
	fulls decimal(8,2),
	cases int not null,
	tags varchar(100),
	object_id varchar(10),
	update_datetime DATETIME not null);

drop table client_product;
create table client_product(
        ID int IDENTITY(1,1) PRIMARY KEY,
        client_name varchar(100) not null,
        name varchar(100) not null,
        size_id int not null,
        serving varchar(20),
        retail_price decimal(8,2),
        update_datetime DATETIME not null);

drop table domain_value;
create table domain_value (
       name varchar(40) not null,
       val varchar(200) not null,
       update_datetime DATETIME not null
);

drop table location;
create table location(
        client_name varchar(100) not null,
        name varchar(100) not null,
        object_id varchar(10),
        update_datetime DATETIME not null
);


drop table recipe;
create table recipe(
        ID int IDENTITY(1,1) PRIMARY KEY,
        client_name varchar(80) not null,
        name varchar(100) not null,
        ignore int NOT NULL DEFAULT (0),
        object_id varchar(10),
        update_datetime DATETIME not null
);

drop table audit_recipe;
create table audit_recipe(
        ID int IDENTITY(1,1) PRIMARY KEY,
        audit_id int not null,
        client_name varchar(80) not null,
        name varchar(100) not null,
        ignore int NOT NULL DEFAULT (0),
        object_id varchar(10),
        update_datetime DATETIME not null
);

drop table recipe_item;
create table recipe_item(
        ID int IDENTITY(1,1) PRIMARY KEY,
        recipe_id int not null,
        product_id int not null,
        fulls decimal(8,2),
        ounces decimal(8,2),
        object_id varchar(10),
        update_datetime DATETIME not null
);

drop table audit_recipe_item;
create table audit_recipe_item(
        ID int IDENTITY(1,1) PRIMARY KEY,
        audit_id int not null,
        recipe_id int not null,
        product_id int not null,
        fulls decimal(8,2),
        ounces decimal(8,2),
        object_id varchar(10),
        update_datetime DATETIME not null
);

drop table sale;
create table sale(
        ID int IDENTITY(1,1) PRIMARY KEY,
        audit_id int not null,
        recipe_id int not null,
        amount decimal(8,2),
        price decimal(8,2),
        object_id varchar(10),
        update_datetime DATETIME not null
);

drop table modifier;
create table modifier (
        ID int IDENTITY(1,1) PRIMARY KEY,
        client_name varchar(80) not null,
        modifier_name varchar(80) not null,
		percentage decimal(8,2),
        object_id varchar(10),
        update_datetime DATETIME not null
)

drop table modifier_item;
create table modifier_item (
        ID int IDENTITY(1,1) PRIMARY KEY,
        modifier_id int not null,
        audit_id int not null,
		product_id int,
		recipe_id int,
        cost decimal(8,2),
        fulls decimal(8,2),
        ounces decimal(8,2),
        object_id varchar(10),
        update_datetime DATETIME not null
)

drop table user_name;
create table user_name (
        ID int IDENTITY(1,1) PRIMARY KEY,
        username varchar(80) not null,
        password varchar(200) not null,
        temp_password varchar(20),
        roles varchar(400) not null,
        update_datetime DATETIME not null
)

#insert into audit_recipe(audit_id, client_name, name, ignore, object_id, update_datetime)
#select audit_id, client_name, name, ignore, recipe.id, update_datetime
from recipe, (select sale.audit_id, sale.recipe_id
from sale
group by audit_id, recipe_id) a
where a.recipe_id=recipe.id;

update sale
set recipe_id = audit_recipe.id
from audit_recipe
where sale.audit_id=audit_recipe.audit_id and sale.recipe_id=audit_recipe.object_id;

insert into audit_recipe_item(audit_id, recipe_id, product_id, fulls, ounces, object_id, update_datetime)
select sale.audit_id, audit_recipe.id, recipe_item.product_id, fulls, ounces, recipe_item.id, sale.update_datetime
from audit_recipe, sale, recipe, recipe_item
where sale.recipe_id=recipe.id
and recipe.id=audit_recipe.object_id
and sale.audit_id=audit_recipe.audit_id
and recipe_item.recipe_id=recipe.id;

insert into audit_recipe_item(audit_id, recipe_id, product_id, fulls, ounces, object_id, update_datetime)
select sale.audit_id, audit_recipe.id, recipe_item.product_id, fulls, ounces, recipe_item.id, recipe_item.update_datetime
from recipe_item, audit_recipe, sale
where sale.recipe_id=audit_recipe.id
and sale.audit_id=audit_recipe.audit_id
and recipe_item.recipe_id=audit_recipe.object_id;

insert into client_product(client_name, name, size_id, serving, update_datetime)
select client.name, product.name, size_id,serving, product.update_datetime
from client, product;