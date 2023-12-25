# Streaming Trajectory Segmentation Based on Stay-Point Detection
***
STEP is  an efficient and effective streaming trajectory algorithm.

## STEP feature
- STEP can rely only on **recent data** and **a few features** to efficiently divide trajectories into meaningful trajectory segments.
- STEP propose a well-designed **grid index** and three areas to prune unnecessary distance calculations effectively, accelerating the process of trajectory segmentation in streaming environments.

## Project Structure
This project mainly includes the following various trajectory segmentation algorithms:

- The main code for the STEP algorithm is in the *org/urbcomp/sts/method/step* package.

- The main code for the SPD algorithm is in the *org/urbcomp/sts/method/spd* package.

- The main code for the SWS algorithm is in the *org/urbcomp/sts/method/sws* package.

For each method there is a corresponding executor, located in the *org/urbcomp/sts/executor* package.

### STEP Structure
STEP consists of three modules, _Input_, _Trajectory Segmentation_ and _Stay Point Detection_, where Stay Point Detection in turn contains two operations _Identifying_ and _Merging_.

#### Stay Point Detection
The purpose of this operation is to detect the streaming stay points of the newly input GPS point, including both the discovery of new stay point and the merging of stay points.

#### Trajectory Segmentation
The purpose of this operation is to segment the trajectory according to the segmentation conditions to reduce the stress on the memory from a large number of trajectory points.

## Test STEP
The default parameters are set according to the table below and can be adjusted for different data sets.

<table>
  <tr>
    <th>Parameter</th>
    <th>Range</th>
    <th>Default</th>
  </tr>
  <tr>
    <th>D</th>
    <th>10m,20m,50m,75m,100m</th>
    <th>50m</th>
  </tr>
  <tr>
    <th>T</th>
    <th>1min,3min,5min,10min,15min</th>
    <th>15min</th>
  </tr>
  <tr>
    <th>n</th>
    <th>1,3,5,7,10,15,20,25,30</th>
    <th>20</th>
  </tr>
  <tr>
    <th>o</th>
    <th>1w,3w,5w,10w</th>
    <th>3w</th>
  </tr>
  <tr>
    <th>window size of SWS</th>
    <th>7</th>
    <th>7</th>
  </tr>
</table>

### Prerequisites for testing

The following resources need to be downloaded and installed:

- Java 8 download: https://www.oracle.com/java/technologies/downloads/#java8
- IntelliJ IDEA download: https://www.jetbrains.com/idea/
- git download:https://git-scm.com/download
- maven download: https://archive.apache.org/dist/maven/maven-3/

Download and install jdk-8, IntelliJ IDEA and git. IntelliJ IDEA's maven project comes with maven, you can also use your
own maven environment, just change it in the settings.

### Clone code

1. Open *IntelliJ IDEA*, find the *git* column, and select *Clone...*

2. In the *Repository URL* interface, *Version control* selects *git*

3. URL filling: *https://github.com/Spatio-Temporal-Lab/StreamingTrajSegment.git*

### Set JDK

File -> Project Structure -> Project -> Project SDK -> *add SDK*

Click *JDK* to select the address where you want to download jdk-8

### Test STEP

Select the org/urbcomp/sts package in the test folder, which includes tests for Latency&Throughput and F1-score.

#### Path Setting 
In order to simulate the streaming of gps points, you can split the data set into multiple files and read them in sequence. For example, write the first GPS point in the data set to the **object-1.txt file** and put it in the **object** folder.

#### Datasets

Geolife dataset can be found in following link，datasets CQ-Taxi and CQ-Hazs are not publicly available for the time being, and public data sets such as T-drive can be used instead.

- Geolife: https://www.microsoft.com/en-us/download/details.aspx?id=52367
- T-drive: https://www.microsoft.com/en-us/research/publication/t-drive-trajectory-data-sample/



