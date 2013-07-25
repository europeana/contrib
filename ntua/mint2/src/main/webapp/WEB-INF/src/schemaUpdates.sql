--helper functions

CREATE OR REPLACE FUNCTION execute(TEXT) RETURNS VOID AS $$

BEGIN EXECUTE $1; END;

$$ LANGUAGE plpgsql STRICT;

CREATE OR REPLACE FUNCTION schema_exists(TEXT) RETURNS bool as $$

    SELECT exists(SELECT 1 FROM information_schema.schemata WHERE schema_name = $1);

$$ language sql STRICT;

CREATE OR REPLACE FUNCTION table_exists(TEXT) RETURNS bool as $$

    SELECT exists(SELECT 1 FROM information_schema.tables WHERE (table_schema, table_name, table_type) = (current_schema(), $1, 'BASE TABLE'));

$$ language sql STRICT;

CREATE OR REPLACE FUNCTION view_exists( TEXT) RETURNS bool as $$

    SELECT exists(SELECT 1 FROM information_schema.views WHERE (table_schema, table_name) = (current_schema(),$1));

$$ language sql STRICT;

CREATE OR REPLACE FUNCTION column_exists(TEXT, TEXT) RETURNS bool as $$

    SELECT exists(SELECT 1 FROM information_schema.columns WHERE (table_schema, table_name, column_name) = (current_schema(), $1, $2));

$$ language sql STRICT;


CREATE OR REPLACE FUNCTION index_exists(TEXT, TEXT) RETURNS bool as $$

    SELECT exists(SELECT 1 FROM pg_catalog.pg_indexes  WHERE (schemaname, tablename, indexname) = (current_schema(), $1, $2));

$$ language sql STRICT;

CREATE OR REPLACE FUNCTION sequence_exists( TEXT ) RETURNS bool as $$

    SELECT exists(SELECT 1 FROM information_schema.sequences WHERE (sequence_schema, sequence_name ) = (current_schema(), $1 ));

$$ language sql STRICT;

-- end helpers




-- add the publish_allowed column to organization
select execute( $$alter table organization add column publish_allowed boolean not null default FALSE$$) 
 where NOT column_exists( 'organization', 'publish_allowed');

-- add a crosswalk to transformation
select execute( $$alter table transformation add column crosswalk_id int references crosswalk$$) 
 where NOT column_exists( 'transformation', 'crosswalk_id');


 -- adding xsl support to mappings
 select execute( $$alter table mapping add column xsl text $$) 
 where NOT column_exists( 'mapping', 'xsl');
 
 -- sequence for key-value store feature
 select execute( $$create sequence seq_meta_id start with 1000$$ )
 where NOT sequence_exists( 'seq_meta_id');
 
 
 -- new publication moves from publication tables to
 -- status on initial dataset.
 select execute( $$alter table dataset add column publication_status text $$) 
 where NOT column_exists( 'dataset', 'publication_status');

 update dataset set publication_status='NOT APPLICABLE' where publication_status is null;
 
 
 select execute( $$alter table dataset add column publish_date timestamp $$) 
 where NOT column_exists( 'dataset', 'publish_date');
 
 select execute( $$ drop table publication $$ )
 where table_exists( 'publication' );

 select execute( $$ drop table publication_input $$ )
 where table_exists('publication_input' );

 
 -- start creating valid_item_counts if they dont exist in the database
 select execute ($$
	 create table tmp_dataset_valid as
       select ds.dataset_id, count(*) as valid_item_count
       from dataset ds, item i
       where i.dataset_id = ds.dataset_id
       and i.valid= true
       group by ds.dataset_id
$$ )
 where NOT column_exists( 'dataset', 'valid_item_count');

select execute( $$alter table dataset add column valid_item_count int $$) 
 where NOT column_exists( 'dataset', 'valid_item_count');
 

select execute( $$
  update dataset ds set valid_item_count = (
	select valid_item_count 
	from tmp_dataset_valid tdv
	where tdv.dataset_id = ds.dataset_id )
$$ )
where table_exists( 'tmp_dataset_valid' );

select execute( $$
  update dataset ds set valid_item_count = 0 where valid_item_count is null
$$ )
where table_exists( 'tmp_dataset_valid' );


select execute( $$ drop table tmp_dataset_valid $$)
where table_exists( 'tmp_dataset_valid' );

-- end creating valid_item_counts
 
select execute( $$ alter table dataset add column published_item_count int NOT NULL default 0 $$ )
where NOT column_exists( 'dataset', 'published_item_count');

-- xpath value index
select execute( $$ create index idx_xpath_stats_values_summary on xpath_stats_values( xpath_summary_id, start ) $$ )
where NOT index_exists( 'xpath_stats_values', 'idx_xpath_stats_values_summary');

-- value edits table 
select execute( $$ create sequence seq_value_edit_id start with 1000 $$ )
where not sequence_exists( 'seq_value_edit_id');

select execute( $$ create table value_edit (
	value_edit_id bigint primary key not null,
	dataset_id int,
	xpath_holder_id int,
	match_string text,
	replace_string text,
	created timestamp
) $$ )
where not table_exists( 'value_edit');

select execute( $$ alter table mapping add column last_modified timestamp $$ )
where not column_exists( 'mapping', 'last_modified');

update mapping set last_modified=creation_date where last_modified is null;

select execute( $$ alter table xml_schema add column last_modified timestamp $$ )
where not column_exists( 'xml_schema', 'last_modified');

update xml_schema set last_modified=created where last_modified is null;

select execute( $$ alter table xml_schema add column json_original text $$ )
where not column_exists( 'xml_schema', 'json_original');

select execute( $$ alter table xml_schema add column schematron_rules text $$ )
where not column_exists( 'xml_schema', 'schematron_rules');

select execute( $$ alter table xml_schema add column schematron_xsl text $$ )
where not column_exists( 'xml_schema', 'schematron_xsl');

