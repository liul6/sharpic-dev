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