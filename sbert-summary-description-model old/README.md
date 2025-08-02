---
tags:
- sentence-transformers
- sentence-similarity
- feature-extraction
- generated_from_trainer
- dataset_size:44600
- loss:CosineSimilarityLoss
base_model: sentence-transformers/all-MiniLM-L6-v2
widget:
- source_sentence: '[hbmlint] Detect repeated columns'
  sentences:
  - specifically to account for http://jira.jboss.com/jira/browse/MPJDOCBOOK-10
  - '(verbatim from the forum , http://forum.hibernate.org/viewtopic.php?t=967007
    ) Hi! I use hbm2ddl (from Hibernate Tools v3.2 beta 8) to create the DB schema
    and noticed, that duplicate column names are not detected. Example: <?xml version="1.0"
    encoding="UTF-8"?> <!DOCTYPE hibernate-mapping PUBLIC "-//Hibernate/Hibernate
    Mapping DTD 3.0//EN" "http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd">
    <hibernate-mapping package="com.manning.hq.stein.data"> <class name="TelefonskiImenik">
    <id name="id" column="imid"> <generator class="native" /> </id> <property name="izdajatelj"
    column="col"/> <property name="verzija" column="col"/> </class> </hibernate-mapping>
    The same thing happens if one column name is implicit : <property name="izdajatelj"
    column="verzija"/> <property name="verzija"/> If I try to use this mapping with
    an application, hibernate (v3.2) fails (in both cases) in buildSessionFactory()
    : Exception in thread'
  - 'Suppose I have the following mapping: ClassA ======= <class name="ClassA" table="TableA"
    lazy="false"> <id name="ID" column="ID" type="integer" unsaved-value="0"> <generator
    class="native"> </generator> </id> <one-to-one name="classB" class="ClassB" cascade="save-update,lock"
    property-ref="classA" lazy="no-proxy" access="field"/> </class> ClassB =======
    <class name="ClassB" table="TableB" lazy="false"> <composite-id> <key-property
    name="ID" column="ID" type="integer"/> <key-many-to-one name="classA" column="ClassAID"
    class="ClassA"> </key-many-to-one> </composite-id> </class> Pre-condition : TableA
    ---- ID ---- 1 ---- TableB ------------+ ID ClassAID ------------+ 1 1 ------------+
    When I use Session.createQuery("From ClassA as ClassA").list(), it throw the following
    exception: java.lang.NullPointerException at org.hibernate.persister.entity.AbstractEntityPersister.loadByUniqueKey(AbstractEntityPersister.java:1641)
    at org.hibernate.type.EntityType.loadByUniqueKey(EntityType.java:608) at org.hibernate.type.EntityType.resolve(EntityType.java:382)
    at org.hibernate.engine.TwoPhaseLoad.initializeEntity(TwoPhaseLoad.java:116) at
    org.hibernate.loader.Loader.initializeEntitiesAndCollections(Loader.java:842)
    at org.hibernate.loader.Loader.doQuery(Loader.java:717) at org.hibernate.loader.Loader.doQueryAndInitializeNonLazyCollections(Loader.java:224)
    at org.hibernate.loader.Loader.doList(Loader.java:2211) at org.hibernate.loader.Loader.listIgnoreQueryCache(Loader.java:2095)
    at org.hibernate.loader.Loader.list(Loader.java:2090) at org.hibernate.loader.hql.QueryLoader.list(QueryLoader.java:375)
    at org.hibernate.hql.ast.QueryTranslatorImpl.list(QueryTranslatorImpl.java:338)'
- source_sentence: Test the Hibernate Search - JSR-352 integration with OGM
  sentences:
  - Currently the spec prohibits configuration of parameter constraints on overriding
    methods in subtypes. This requirement can pose a problem when interception technologies
    such as CDI create subclass proxies representing validated objects. As configuring
    the same constraints in a subtype method does not really represent a violation
    of LSV, I think we should allow this. The same might apply to group conversions
    and marking return values for cascaded validation. Potentially also methods in
    parallel hierarchies, but as this does not influence the proxy case, we could
    leave it to impls to allow this.
  - 'When the meta-tag "generated-class" is added to the class definition in a hbm.xml
    file and the file is formatted (e.g. by an IDE) so that there are spaces/CR/LF
    between the tag and its value then hbm2java terminates with the following error:
    build.xml: ... : org.hibernate.tool.hbm2x.ExporterException: Error while processing
    template the second time See the following discussion for more details: http://forum.hibernate.org/viewtopic.php?p=2266174#2266174'
  - 'In 2 other places a null check is done: final CollectionPersister[] collectionPersisters
    = getCollectionPersisters(); if ( collectionPersisters != null ) { but in handleEmptyCollections
    there is no null check causing a null pointer exception because getCollectionPersisters()
    always currently returns null... CollectionPersister[] collectionPersisters =
    getCollectionPersisters(); for ( int j=0; j<collectionPersisters.length; j++ )
    {'
- source_sentence: .CollectionBinder.bindManytoManyInverseFk does not handling exceptions.
  sentences:
  - 'It appears that AbstractEntityPersister does not implement Loadable.getTableName().
    If I insert a dummy implementation the app will start. [9/22/03 13:27:02:585 CDT]
    5e254dcc WebGroup E SRVE0020E: [Servlet Error]-[action]: Failed to load servlet:
    java.lang.AbstractMethodError: net/sf/hibernate/persister/AbstractEntityPersister.getTableName
    at net.sf.hibernate.loader.OuterJoinLoader.walkAssociationTree(OuterJoinLoader.java:447)
    at net.sf.hibernate.loader.OuterJoinLoader.walkAssociationTree(OuterJoinLoader.java:183)
    at net.sf.hibernate.loader.OuterJoinLoader.walkClassTree(OuterJoinLoader.java:214)
    at net.sf.hibernate.loader.OuterJoinLoader.walkTree(OuterJoinLoader.java:86) at
    net.sf.hibernate.loader.OneToManyLoader.<init>(OneToManyLoader.java:54) at net.sf.hibernate.loader.OneToManyLoader.<init>(OneToManyLoader.java:39)
    at net.sf.hibernate.collection.CollectionPersister.createCollectionInitializer(CollectionPersister.java:324)
    at net.sf.hibernate.collection.CollectionPersister.<init>(CollectionPersister.java:297)
    at net.sf.hibernate.impl.SessionFactoryImpl.<init>(SessionFactoryImpl.java:138)
    at net.sf.hibernate.cfg.Configuration.buildSessionFactory(Configuration.java:660)
    at us.il.state.idpa.oigcase.controller.OIGPlugin.init(OIGPlugin.java:128) at org.apache.struts.action.ActionServlet.initModulePlugIns(ActionServlet.java:1158)
    at org.apache.struts.action.ActionServlet.init(ActionServlet.java:473) at javax.servlet.GenericServlet.init(GenericServlet.java:258)
    at com.ibm.ws.webcontainer.servlet.StrictServletInstance.doInit(StrictServletInstance.java:82)
    at com.ibm.ws.webcontainer.servlet.StrictLifecycleServlet._init(StrictLifecycleServlet.java:147)
    at com.ibm.ws.webcontainer.servlet.PreInitializedServletState.init(StrictLifecycleServlet.java:270)
    at com.ibm.ws.webcontainer.servlet.StrictLifecycleServlet.init(StrictLifecycleServlet.java:113)
    at com.ibm.ws.webcontainer.servlet.ServletInstance.init(ServletInstance.java:189)
    at javax.servlet.GenericServlet.init(GenericServlet.java:258) at com.ibm.ws.webcontainer.webapp.WebAppServletManager.addServlet(WebAppServletManager.java:903)
    at com.ibm.ws.webcontainer.webapp.WebAppServletManager.loadServlet(WebAppServletManager.java:266)
    at com.ibm.ws.webcontainer.webapp.WebAppServletManager.loadAutoLoadServlets(WebAppServletManager.java:583)
    at com.ibm.ws.webcontainer.webapp.WebApp.loadServletManager(WebApp.java:1252)
    at com.ibm.ws.webcontainer.webapp.WebApp.init(WebApp.java:274) at com.ibm.ws.webcontainer.srt.WebGroup.loadWebApp(WebGroup.java:345)
    at com.ibm.ws.webcontainer.srt.WebGroup.init(WebGroup.java:208) at com.ibm.ws.webcontainer.WebContainer.addWebApplication(WebContainer.java:968)
    at com.ibm.ws.runtime.component.WebContainerImpl.install(WebContainerImpl.java:133)
    at com.ibm.ws.runtime.component.WebContainerImpl.start(WebContainerImpl.java:360)
    at com.ibm.ws.runtime.component.ApplicationMgrImpl.start(ApplicationMgrImpl.java:397)
    at com.ibm.ws.runtime.component.DeployedApplicationImpl.fireDeployedObjectStart(DeployedApplicationImpl.java:751)
    at com.ibm.ws.runtime.component.DeployedModuleImpl.start(DeployedModuleImpl.java:347)
    at'
  - '.CollectionBinder.bindManytoManyInverseFk has the following code: public static
    void bindManytoManyInverseFk(PersistentClass referencedEntity, Ejb3JoinColumn[]
    columns, SimpleValue value, boolean unique, MetadataBuildingContext buildingContext)
    { ... final Property property = referencedEntity.getRecursiveProperty( mappedBy
    ); .. } The referencedEntity.getRecursiveProperty can throw MappingException.
    The other method of this class catches and rethrows AnnotationException: protected
    boolean bindStarToManySecondPass( Map<String, PersistentClass> persistentClasses,
    XClass collType, Ejb3JoinColumn[] fkJoinColumns, Ejb3JoinColumn[] keyColumns,
    Ejb3JoinColumn[] inverseColumns, Ejb3Column[] elementColumns, boolean isEmbedded,
    XProperty property, boolean unique, TableBinder associationTableBinder, boolean
    ignoreNotFound, MetadataBuildingContext buildingContext) { ... try { reversePropertyInJoin
    = 0 != persistentClass.getJoinNumber( persistentClass.getRecursiveProperty( this.mappedBy
    ) ); } catch (MappingException e) { throw new AnnotationException( "mappedBy reference
    an unknown target entity property:'
  - 'I using hibetnate , struts, spring with database is oracle 10.2.0. But when excute
    a statement this below so occur error: SELECT tmsystem_01.ID as id, tmsystem_01.NAME
    as name FROM TM.SYSTEM tmsystem_01 But when i repaire this sql below , so no error:
    SELECT tmsystem_01.ID as id, tmsystem_01.NAME as name FROM "TM.SYSTEM" tmsystem_01
    please give me advance and resolve it.'
- source_sentence: Update README and migration notes to indicate changes in Java compatibility
    in 5.1 branch
  sentences:
  - 'I subclass Customer that has a EmbeddedId using JOIN inheritance strategy. My
    embeddable class has @ManyToOne relationship. Look the classes: Pesson class:
    @Entity @Table(name="person") public class Person { @Id private Integer personId;
    private String name; } Embeddable class ID. @Embeddable public class PersonPk
    implements Serializable { @ManyToOne(optional=false, cascade= {CascadeType.PERSIST,
    CascadeType.REFRESH, CascadeType.REMOVE} , fetch=FetchType.EAGER) @JoinColumn(name="personId")
    private Person person; // ... getters and setters } CustomerClass: @Entity @Table(name="Customer")
    @Inheritance(strategy=InheritanceType.JOINED) public class Customer { @Id private
    PersonPk pk = new PersonPk(); } Regional Custumer: @Entity @Table(name="AmericanCustomer")
    @PrimaryKeyJoinColumn(name="personId") @IdClass(PessoaPk.class) public class AmericanCustomer
    extends Customer { // some american customer fields.. } This mapping throw'
  - 'The following changed Java compatibility to 1.7: In 5.1.4, java.util.Objects
    was introduced by an unused import statement into ActionQueue by HHH-9864 Closed
    . This seemed to only affect only some versions of Java 1.6. In 5.1.11, java.util.regex.Pattern.UNICODE_CHARACTER_CLASS
    was introduced into ReflectHelper by HHH-11377 Closed . The following changed
    Java compatibility to 1.8: In 5.1.17, the upgrade to dom4j to 2.2.1 by HHH-12964
    Closed . The README and migration notes should reflect these changes.'
  - something complex Steve has in his head http://www.cs.arizona.edu/people/rts/tdbbook.pdf
    section 2.3 Bitemporal Tables
- source_sentence: Persist OneToOne associations as singular attribute in MongoDB
  sentences:
  - H allows me to set mappedBy="234ADL~SKA0DS~MONKEY" http://forum.hibernate.org/viewtopic.php?p=2264879#2264879
    This fix should not only check that the property exists, but that it is persistent
    and has the correct signature as well.
  - 'We are using a single address table with InheritanceType.SINGLE_TABLE and a simple
    discriminator value: @Entity @Table(name = "CUSTOMER_ADDRESS") @Inheritance(strategy
    = InheritanceType.SINGLE_TABLE) @DiscriminatorColumn(name="address_type", discriminatorType
    = DiscriminatorType.STRING) public abstract class CustomerAddressEntity {...}
    @Entity @DiscriminatorValue("H") public class CustomerAddressLegalEntity extends
    CustomerAddressEntity {...} @Entity @DiscriminatorValue("V") public class CustomerAddressMailingEntity
    extends CustomerAddressEntity {...} We are using a query that selects a single
    case, the customer assigned to the case and the addresses of the customer: SELECT
    ca FROM CaseEntity ca LEFT JOIN FETCH ca.customer cu LEFT JOIN FETCH cu.customerAddressLegal
    cal LEFT JOIN FETCH cu.customerAddressMailing cam WHERE ca.caseId = :caseId When
    we used Spring Boot 2.x, the generated'
  - 'See comments on https://hibernate.atlassian.net/browse/HSEARCH-4867 : its a bit
    complicated as early attempts show that running AWS OpenSearch Serverless constantly
    would cost us a lot of money.'
pipeline_tag: sentence-similarity
library_name: sentence-transformers
metrics:
- pearson_cosine
- spearman_cosine
model-index:
- name: SentenceTransformer based on sentence-transformers/all-MiniLM-L6-v2
  results:
  - task:
      type: semantic-similarity
      name: Semantic Similarity
    dataset:
      name: validation set evaluator
      type: validation_set_evaluator
    metrics:
    - type: pearson_cosine
      value: 0.8735656464975768
      name: Pearson Cosine
    - type: spearman_cosine
      value: 0.8375979322659031
      name: Spearman Cosine
---

# SentenceTransformer based on sentence-transformers/all-MiniLM-L6-v2

This is a [sentence-transformers](https://www.SBERT.net) model finetuned from [sentence-transformers/all-MiniLM-L6-v2](https://huggingface.co/sentence-transformers/all-MiniLM-L6-v2). It maps sentences & paragraphs to a 384-dimensional dense vector space and can be used for semantic textual similarity, semantic search, paraphrase mining, text classification, clustering, and more.

## Model Details

### Model Description
- **Model Type:** Sentence Transformer
- **Base model:** [sentence-transformers/all-MiniLM-L6-v2](https://huggingface.co/sentence-transformers/all-MiniLM-L6-v2) <!-- at revision fa97f6e7cb1a59073dff9e6b13e2715cf7475ac9 -->
- **Maximum Sequence Length:** 256 tokens
- **Output Dimensionality:** 384 dimensions
- **Similarity Function:** Cosine Similarity
<!-- - **Training Dataset:** Unknown -->
<!-- - **Language:** Unknown -->
<!-- - **License:** Unknown -->

### Model Sources

- **Documentation:** [Sentence Transformers Documentation](https://sbert.net)
- **Repository:** [Sentence Transformers on GitHub](https://github.com/UKPLab/sentence-transformers)
- **Hugging Face:** [Sentence Transformers on Hugging Face](https://huggingface.co/models?library=sentence-transformers)

### Full Model Architecture

```
SentenceTransformer(
  (0): Transformer({'max_seq_length': 256, 'do_lower_case': False}) with Transformer model: BertModel 
  (1): Pooling({'word_embedding_dimension': 384, 'pooling_mode_cls_token': False, 'pooling_mode_mean_tokens': True, 'pooling_mode_max_tokens': False, 'pooling_mode_mean_sqrt_len_tokens': False, 'pooling_mode_weightedmean_tokens': False, 'pooling_mode_lasttoken': False, 'include_prompt': True})
  (2): Normalize()
)
```

## Usage

### Direct Usage (Sentence Transformers)

First install the Sentence Transformers library:

```bash
pip install -U sentence-transformers
```

Then you can load this model and run inference.
```python
from sentence_transformers import SentenceTransformer

# Download from the 🤗 Hub
model = SentenceTransformer("sentence_transformers_model_id")
# Run inference
sentences = [
    'Persist OneToOne associations as singular attribute in MongoDB',
    'We are using a single address table with InheritanceType.SINGLE_TABLE and a simple discriminator value: @Entity @Table(name = "CUSTOMER_ADDRESS") @Inheritance(strategy = InheritanceType.SINGLE_TABLE) @DiscriminatorColumn(name="address_type", discriminatorType = DiscriminatorType.STRING) public abstract class CustomerAddressEntity {...} @Entity @DiscriminatorValue("H") public class CustomerAddressLegalEntity extends CustomerAddressEntity {...} @Entity @DiscriminatorValue("V") public class CustomerAddressMailingEntity extends CustomerAddressEntity {...} We are using a query that selects a single case, the customer assigned to the case and the addresses of the customer: SELECT ca FROM CaseEntity ca LEFT JOIN FETCH ca.customer cu LEFT JOIN FETCH cu.customerAddressLegal cal LEFT JOIN FETCH cu.customerAddressMailing cam WHERE ca.caseId = :caseId When we used Spring Boot 2.x, the generated',
    'H allows me to set mappedBy="234ADL~SKA0DS~MONKEY" http://forum.hibernate.org/viewtopic.php?p=2264879#2264879 This fix should not only check that the property exists, but that it is persistent and has the correct signature as well.',
]
embeddings = model.encode(sentences)
print(embeddings.shape)
# [3, 384]

# Get the similarity scores for the embeddings
similarities = model.similarity(embeddings, embeddings)
print(similarities.shape)
# [3, 3]
```

<!--
### Direct Usage (Transformers)

<details><summary>Click to see the direct usage in Transformers</summary>

</details>
-->

<!--
### Downstream Usage (Sentence Transformers)

You can finetune this model on your own dataset.

<details><summary>Click to expand</summary>

</details>
-->

<!--
### Out-of-Scope Use

*List how the model may foreseeably be misused and address what users ought not to do with the model.*
-->

## Evaluation

### Metrics

#### Semantic Similarity

* Dataset: `validation_set_evaluator`
* Evaluated with [<code>EmbeddingSimilarityEvaluator</code>](https://sbert.net/docs/package_reference/sentence_transformer/evaluation.html#sentence_transformers.evaluation.EmbeddingSimilarityEvaluator)

| Metric              | Value      |
|:--------------------|:-----------|
| pearson_cosine      | 0.8736     |
| **spearman_cosine** | **0.8376** |

<!--
## Bias, Risks and Limitations

*What are the known or foreseeable issues stemming from this model? You could also flag here known failure cases or weaknesses of the model.*
-->

<!--
### Recommendations

*What are recommendations with respect to the foreseeable issues? For example, filtering explicit content.*
-->

## Training Details

### Training Dataset

#### Unnamed Dataset


* Size: 44,600 training samples
* Columns: <code>sentence_0</code>, <code>sentence_1</code>, and <code>label</code>
* Approximate statistics based on the first 1000 samples:
  |         | sentence_0                                                                        | sentence_1                                                                         | label                                                         |
  |:--------|:----------------------------------------------------------------------------------|:-----------------------------------------------------------------------------------|:--------------------------------------------------------------|
  | type    | string                                                                            | string                                                                             | float                                                         |
  | details | <ul><li>min: 4 tokens</li><li>mean: 16.73 tokens</li><li>max: 64 tokens</li></ul> | <ul><li>min: 5 tokens</li><li>mean: 135.2 tokens</li><li>max: 256 tokens</li></ul> | <ul><li>min: 0.0</li><li>mean: 0.5</li><li>max: 1.0</li></ul> |
* Samples:
  | sentence_0                                                                           | sentence_1                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               | label            |
  |:-------------------------------------------------------------------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:-----------------|
  | <code>Provide separate hibernate2-collection.jar</code>                              | <code>It would be good to provide a separate hibernate2-collection.jar which just contains the minimal set of classes that's required to handle Hibernate's collection implementations. The typical usage would be remote clients that receive serialized persistent objects via RMI: They need to know about Hibernate's collection classes but do not have any other Hibernate dependencies. Juergen</code>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            | <code>1.0</code> |
  | <code>Support callbacks defined on superclasses of an applied entity-listener</code> | <code>This is a request to support looking for callbacks on the superclasses of an applied entity-listener. E.g.: _x000D_ class MyListenerBase {_x000D_ @PrePersist_x000D_ public void prePersistCallback(Object theEntity) {_x000D_ ..._x000D_ }_x000D_ }_x000D_ _x000D_ class MyListenerSub extends MyListenerBase {_x000D_ @PreRemove_x000D_ public void preRemoveCallback(Object theEntity) {_x000D_ ..._x000D_ }_x000D_ }_x000D_ _x000D_ @Entity_x000D_ @EntityListeners(MyListenerSub.class)_x000D_ class MyEntity {_x000D_ ..._x000D_ }_x000D_ We need to be careful in dealing with overrides though. E.g.: _x000D_ class MyListenerBase {_x000D_ @PrePersist_x000D_ public void prePersistCallback(Object theEntity) {_x000D_ ..._x000D_ }_x000D_ }_x000D_ _x000D_ class MyListenerSub extends MyListenerBase {_x000D_ @Override_x000D_ @PrePersist_x000D_ public void prePersistCallback(Object theEntity) {_x000D_ ..._x000D_ }_x000D_ }_x000D_ _x000D_ @Entity_x000D_ @EntityListeners(MyListenerSub.class)_x000D_ class MyEntity ...</code> | <code>1.0</code> |
  | <code>Improve integration with Java's security manager</code>                        | <code>Currently we wrap all reflection calls in PrivilegedAction. This way Validators need the following grants in the policy file: grant codeBase "file:/path/to/hibernate-validator-5.1.1.Final.jar" {_x000D_ permission java.lang.reflect.ReflectPermission "suppressAccessChecks";_x000D_ permission java.lang.RuntimePermission "accessDeclaredMembers";_x000D_ ..._x000D_ };_x000D_ However, this also means that a user might now use ReflectionHelper to execute reflection calls which otherwise would be no allowed. To prevent this we need a Validator specific permission type. Something like this: _x000D_ class ReflectionHelper {_x000D_ public static Field getDeclaredField(Class<?> clazz, String fieldName) {_x000D_ SecurityManager securityManager = System.getSecurityManager();_x000D_ _x000D_ if ( securityManager != null ) {_x000D_ securityManager.checkPermission( HibernateValidatorInternalPermission.INSTANCE );_x000D_ }_x000D_ ..._x000D_ }_x000D_ }_x000D_</code>                                                    | <code>1.0</code> |
* Loss: [<code>CosineSimilarityLoss</code>](https://sbert.net/docs/package_reference/sentence_transformer/losses.html#cosinesimilarityloss) with these parameters:
  ```json
  {
      "loss_fct": "torch.nn.modules.loss.MSELoss"
  }
  ```

### Training Hyperparameters
#### Non-Default Hyperparameters

- `eval_strategy`: steps
- `per_device_train_batch_size`: 32
- `per_device_eval_batch_size`: 32
- `fp16`: True
- `multi_dataset_batch_sampler`: round_robin

#### All Hyperparameters
<details><summary>Click to expand</summary>

- `overwrite_output_dir`: False
- `do_predict`: False
- `eval_strategy`: steps
- `prediction_loss_only`: True
- `per_device_train_batch_size`: 32
- `per_device_eval_batch_size`: 32
- `per_gpu_train_batch_size`: None
- `per_gpu_eval_batch_size`: None
- `gradient_accumulation_steps`: 1
- `eval_accumulation_steps`: None
- `torch_empty_cache_steps`: None
- `learning_rate`: 5e-05
- `weight_decay`: 0.0
- `adam_beta1`: 0.9
- `adam_beta2`: 0.999
- `adam_epsilon`: 1e-08
- `max_grad_norm`: 1
- `num_train_epochs`: 3
- `max_steps`: -1
- `lr_scheduler_type`: linear
- `lr_scheduler_kwargs`: {}
- `warmup_ratio`: 0.0
- `warmup_steps`: 0
- `log_level`: passive
- `log_level_replica`: warning
- `log_on_each_node`: True
- `logging_nan_inf_filter`: True
- `save_safetensors`: True
- `save_on_each_node`: False
- `save_only_model`: False
- `restore_callback_states_from_checkpoint`: False
- `no_cuda`: False
- `use_cpu`: False
- `use_mps_device`: False
- `seed`: 42
- `data_seed`: None
- `jit_mode_eval`: False
- `use_ipex`: False
- `bf16`: False
- `fp16`: True
- `fp16_opt_level`: O1
- `half_precision_backend`: auto
- `bf16_full_eval`: False
- `fp16_full_eval`: False
- `tf32`: None
- `local_rank`: 0
- `ddp_backend`: None
- `tpu_num_cores`: None
- `tpu_metrics_debug`: False
- `debug`: []
- `dataloader_drop_last`: False
- `dataloader_num_workers`: 0
- `dataloader_prefetch_factor`: None
- `past_index`: -1
- `disable_tqdm`: False
- `remove_unused_columns`: True
- `label_names`: None
- `load_best_model_at_end`: False
- `ignore_data_skip`: False
- `fsdp`: []
- `fsdp_min_num_params`: 0
- `fsdp_config`: {'min_num_params': 0, 'xla': False, 'xla_fsdp_v2': False, 'xla_fsdp_grad_ckpt': False}
- `fsdp_transformer_layer_cls_to_wrap`: None
- `accelerator_config`: {'split_batches': False, 'dispatch_batches': None, 'even_batches': True, 'use_seedable_sampler': True, 'non_blocking': False, 'gradient_accumulation_kwargs': None}
- `deepspeed`: None
- `label_smoothing_factor`: 0.0
- `optim`: adamw_torch
- `optim_args`: None
- `adafactor`: False
- `group_by_length`: False
- `length_column_name`: length
- `ddp_find_unused_parameters`: None
- `ddp_bucket_cap_mb`: None
- `ddp_broadcast_buffers`: False
- `dataloader_pin_memory`: True
- `dataloader_persistent_workers`: False
- `skip_memory_metrics`: True
- `use_legacy_prediction_loop`: False
- `push_to_hub`: False
- `resume_from_checkpoint`: None
- `hub_model_id`: None
- `hub_strategy`: every_save
- `hub_private_repo`: False
- `hub_always_push`: False
- `gradient_checkpointing`: False
- `gradient_checkpointing_kwargs`: None
- `include_inputs_for_metrics`: False
- `include_for_metrics`: []
- `eval_do_concat_batches`: True
- `fp16_backend`: auto
- `push_to_hub_model_id`: None
- `push_to_hub_organization`: None
- `mp_parameters`: 
- `auto_find_batch_size`: False
- `full_determinism`: False
- `torchdynamo`: None
- `ray_scope`: last
- `ddp_timeout`: 1800
- `torch_compile`: False
- `torch_compile_backend`: None
- `torch_compile_mode`: None
- `dispatch_batches`: None
- `split_batches`: None
- `include_tokens_per_second`: False
- `include_num_input_tokens_seen`: False
- `neftune_noise_alpha`: None
- `optim_target_modules`: None
- `batch_eval_metrics`: False
- `eval_on_start`: False
- `use_liger_kernel`: False
- `eval_use_gather_object`: False
- `average_tokens_across_devices`: False
- `prompts`: None
- `batch_sampler`: batch_sampler
- `multi_dataset_batch_sampler`: round_robin

</details>

### Training Logs
| Epoch  | Step | Training Loss | validation_set_evaluator_spearman_cosine |
|:------:|:----:|:-------------:|:----------------------------------------:|
| 0.3587 | 500  | 0.0979        | 0.8275                                   |
| 0.7174 | 1000 | 0.0836        | 0.8338                                   |
| 1.0    | 1394 | -             | 0.8338                                   |
| 1.0760 | 1500 | 0.078         | 0.8367                                   |
| 1.4347 | 2000 | 0.0679        | 0.8358                                   |
| 1.7934 | 2500 | 0.0675        | 0.8364                                   |
| 2.0    | 2788 | -             | 0.8363                                   |
| 2.1521 | 3000 | 0.0642        | 0.8370                                   |
| 2.5108 | 3500 | 0.0596        | 0.8363                                   |
| 2.8694 | 4000 | 0.0615        | 0.8376                                   |


### Framework Versions
- Python: 3.10.12
- Sentence Transformers: 3.3.1
- Transformers: 4.46.3
- PyTorch: 2.5.1+cu121
- Accelerate: 1.1.1
- Datasets: 3.1.0
- Tokenizers: 0.20.3

## Citation

### BibTeX

#### Sentence Transformers
```bibtex
@inproceedings{reimers-2019-sentence-bert,
    title = "Sentence-BERT: Sentence Embeddings using Siamese BERT-Networks",
    author = "Reimers, Nils and Gurevych, Iryna",
    booktitle = "Proceedings of the 2019 Conference on Empirical Methods in Natural Language Processing",
    month = "11",
    year = "2019",
    publisher = "Association for Computational Linguistics",
    url = "https://arxiv.org/abs/1908.10084",
}
```

<!--
## Glossary

*Clearly define terms in order to be accessible across audiences.*
-->

<!--
## Model Card Authors

*Lists the people who create the model card, providing recognition and accountability for the detailed work that goes into its construction.*
-->

<!--
## Model Card Contact

*Provides a way for people who have updates to the Model Card, suggestions, or questions, to contact the Model Card authors.*
-->