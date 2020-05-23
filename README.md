# Discovering Automatable Routines from UI Logs via Sequential Pattern Mining
This project was developed as a part of my Master's Thesis at the University of Tartu, Estonia. 
In this thesis, we introduced an approach for discovering automatable routines 
from user interaction logs.  

The approach consists of two modules:
* Routines identification (The module is responsible for discovering candidate routines for automation by applying sequential pattern mining).
* Routine automatability assessment (The module is responsible for analyzing identified candidates on the subject of amenability to automation).  

## Dataset
The CPNs and synthetic logs are available at https://doi.org/10.6084/m9.figshare.7850918.v1  
The controlled-setting logs are available at https://doi.org/10.6084/m9.figshare.12307631.v2  

## Related projects
The approach requires [Foofah](https://github.com/umich-dbgroup/foofah) and [Tane](https://www.cs.helsinki.fi/research/fdk/datamining/tane/) projects.

## Results Reproduction
Get project repository:  
```bash
$ git clone https://github.com/stdevi/CohesionBasedRoutineDiscovery.git
```  
Navigate to the project folder:  
```bash
$ cd CohesionBasedRoutineDiscovery
```  
Run the maven command:  
```bash
$ mvn exec:java -Dexec.mainClass=Main -Dexec.args="path-to-log path-to-foofah path-to-tane"`
```  
where `path-to-log` is the specified path to the dataset, `path-to-foofah` is the specified path to the Foofah project, and `path-to-tane` is the specified path to the Tane project.  

For example, if we place Foofah and Tane projects in the project folder:
```bash
. 
└── CohesionBasedRoutineDiscovery
    ├── foofah
    ├── tane-1.0
    ├── scr
    │    └──...
    ├── .gitignore
    ├── README.md
    └── pom.xml
```
the command to run a solution will be as follows:
```bash
$ mvn exec:java -Dexec.mainClass=Main -Dexec.args="C:\Users\stdevi\logs\Log10.csv foofah tane-1.0"
```