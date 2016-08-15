import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import weka.core.converters.ArffSaver;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * @author atifraza
 *
 */
public class UCR2ARFF {
    
    public static void main(String[] args) {
        String srcDir = null, dstDir = null;
        
        Path pathStr = Paths.get(System.getProperty("user.dir")).resolve("params.properties");
        
        try (BufferedReader brProps = Files.newBufferedReader(pathStr)) {
            Properties dataDirs = new Properties();
            dataDirs.load(brProps);
            srcDir = dataDirs.getProperty("src");
            dstDir = dataDirs.getProperty("dst");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        
        String[] splits = {"_TRAIN", "_TEST"};
        
        List<ArrayList<Double>> dsRaw = null;
        List<Integer> tsClass = new ArrayList<>();
        List<String> classes = new ArrayList<>();
        ArrayList<Double> tsRaw;
        
        String currLine;
        StringTokenizer st;
        for(String dsName : args) {
            for (String split : splits) {
                tsClass.clear();
                try (BufferedReader br = Files.newBufferedReader(Paths.get(srcDir).resolve(dsName+split))) {
                    dsRaw = new ArrayList<>();
                    while ((currLine = br.readLine()) != null) {
                        if (currLine.matches("\\s*")) {
                            continue;
                        } else {
                            currLine = currLine.trim();
                            st = new StringTokenizer(currLine, String.valueOf(" "));
                            tsClass.add((int)Double.parseDouble(st.nextToken()));
                            if (!classes.contains(tsClass.get(tsClass.size()-1).toString())) {
                                classes.add(tsClass.get(tsClass.size()-1).toString());
                            }
                            tsRaw = new ArrayList<>();
                            while (st.hasMoreTokens()) {
                                tsRaw.add(Double.parseDouble(st.nextToken()));
                            }
                            dsRaw.add(tsRaw);
                        }
                    }
                    br.close();
                    
                    ArrayList<Attribute> attInfo = new ArrayList<>();
                    for (int i=0; i<dsRaw.get(0).size(); i++) {
                        attInfo.add(new Attribute("att"+i));
                    }
                    attInfo.add(new Attribute("target", classes));
                    Instances dsWeka = new Instances(dsName, attInfo, dsRaw.size());
                    dsWeka.setClassIndex(dsWeka.numAttributes()-1);
                    Instance tsWeka;
                    int i, j;
                    for (i=0; i<dsRaw.size(); i++) {
                        tsWeka = new DenseInstance(dsRaw.get(i).size()+1);
                        for (j=0; j<dsRaw.get(i).size(); j++) {
                            tsWeka.setValue(j, dsRaw.get(i).get(j));
                        }
                        tsWeka.setDataset(dsWeka);
                        tsWeka.setClassValue(tsClass.get(i).toString());
                        dsWeka.add(tsWeka);
                    }
                    
                    ArffSaver saver = new ArffSaver();
                    saver.setMaxDecimalPlaces(8);
                    saver.setInstances(dsWeka);
                    saver.setFile(new File(Paths.get(dstDir).resolve(dsName+split+".arff").toString()));
                    saver.writeBatch();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
