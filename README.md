# UCR2ARFF

`UCR2ARFF` is a simple program for converting the [UCR Archive] Datasets to [Weka] ARFF files.

The source and destination directory paths are provided through the `params.properties` file. The program takes the Dataset names as CLI arguments and iterates over each of them.

## Program execution Example

    java -jar ucr2arff.jar ds1 ds2 ds3

   [UCR Archive]: <http://www.cs.ucr.edu/~eamonn/time_series_data/>
   [Weka]: <http://www.cs.waikato.ac.nz/ml/weka/index.html>
