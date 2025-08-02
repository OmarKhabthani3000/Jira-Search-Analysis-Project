# Jira Search and Analysis

## 📖 Description

This project is an end-of-studies data engineering and analytics solution that focuses on building a semantic search engine for Jira project data using Elasticsearch. It combines ETL pipelines, machine learning, and data visualization to allow efficient and meaningful search across Jira issues, comments, and project metadata.

The workflow includes:

- Extracting and transforming data from Jira using Talend and Python

- Loading the data into PostgreSQL and indexing it in Elasticsearch

- Fine-tuning a Sentence-BERT (SBERT) model to enable semantic search

- Deploying a search interface with Flask and HTML/CSS/JS

- Visualizing key project metrics using Power BI and Kibana

This system enables users to perform advanced semantic queries and gain insights from Jira data through interactive dashboards and a custom-built search engine.

## 🛠️ Tech Stack

- Jupyter Notebook (Python): Used for ETL (Extract, Transform, Load) processes, allowing you to write and execute Python scripts to extract, transform, and load data into formats suitable for exploitation.

- Microsoft Excel: Used for the initial storage of data, especially during the collection or preparation phases before integration into more complex tools.

- Talend: A powerful data integration platform used to automate the import and transformation of information from multiple sources into a database or data warehouse.

- PostgreSQL: A robust relational database used for structured data storage, enabling fast and efficient access for the later stages of the project.

- WSL (Windows Subsystem for Linux): Used to establish a connection between PostgreSQL and Elasticsearch, facilitating interactions and data integration between the two systems.

- Elasticsearch: A distributed search and analytics engine used to index and quickly search the collected data, offering optimal performance for the project's needs.

- Kibana: The graphical interface of Elasticsearch, used to visualize, explore, and analyze indexed data through interactive dashboards.

- Visual Studio Code (VSCode): A lightweight and efficient code editor used to develop the search solution in Python, integrating Elasticsearch to ensure optimal performance.

- Jira Hibernate: The main data source, used to collect information on projects, issues, and comments.

- SentenceTransformers (Python Library): A powerful Python library used for fine-tuning the SBERT model and generating the embeddings necessary for semantic search.

- Google Colab: Used to train the SBERT model, requiring greater computational power than what is available locally.

- Power BI: A data visualization platform used to create interactive dashboards from the data warehouse.

- Flask (Python Framework): Used to integrate the Elasticsearch search application with the front end, enabling dynamic queries and responses via an API.

- HTML/CSS/JavaScript: Technologies used to develop the platform's front-end interface, offering intuitive navigation between search features and Power BI dashboards.

##For more Informations Check Rapport Omar Khabthani pdf File





