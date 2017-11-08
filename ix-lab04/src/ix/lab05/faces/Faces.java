package ix.lab05.faces;

import utils.Common;
import utils.EigenDecomposition;
import utils.FacesUtils;
import utils.NotYetImplementedException;
import Jama.Matrix;

@SuppressWarnings("unused")
public class Faces {

    /**
     * Computes the variance of the dataset along each dimension (i.e. each  column).
     * @return an array of doubles containing the variance for each column
     */
    public static double[] variance(Matrix data) {
        int M = data.getColumnDimension(); //50
        int N = data.getRowDimension();  //5000
       // double sum=0.0;
        double[] variances = new double[M];
        
        
        Matrix centeredMatrix = Common.center(data);
   
        for(int i =0; i< M;i++){
        	double sum=0.0;
        	for(int j=0;j<N;j++){
        		//System.err.println("value of J:"+j);
        	sum += Math.pow(centeredMatrix.get(j, i),2);
        	}
        	
        	variances[i]=  sum /N;
       
        }
        
        return variances;
        // TODO Complete the function.
         // Remove.

        // Don't forget to center the data!
        // Hint: util.Common provides a few handy functions.

        //return variances;
    }


    /**
     * Computes the PCA of the dataset (i.e. the eigendecomposition of
     * the covariance matrix.)
     * @return a container with with the result of the PCA
     */
    public static PCAResult pca(Matrix data) {
       // PCAResult result = null;
    	  Matrix centeredMatrix = Common.center(data);
    	  double value = 1/(double)data.getRowDimension();
    	  Matrix dataOne = centeredMatrix.transpose().times(value).times(centeredMatrix);
  
          
       EigenDecomposition ed = new EigenDecomposition(dataOne);
       PCAResult result = new PCAResult(ed.eigenvectors,ed.eigenvalues);
       return result;
        // TODO Complete the function.
       //throw new NotYetImplementedException();  // Remove.

        // Note: the class Jama.Matrix defines several handy methods. Using them,
        // you should only need a couple of lines to complete this function.
        //
        // You also might find utils.EigenDecomposition useful.

        //return result;
    }


    /**
     * Projects the dataset on the new basis formed by the PCA.
     * @return a new matrix with the projected version of the data
     */
    public static Matrix project(Matrix data) {
        PCAResult result = pca(data);
        Matrix projected = result.rotation;
        
        Matrix centeredMatrix = Common.center(data);
        return centeredMatrix.times(projected);
        
        
       
        // TODO Complete the function.
        // Warning: don't forget to center the data before projecting it!
       
        
        
        //throw new NotYetImplementedException();  // Remove.

        //return projected;
    }


    /** Use this function to run the various parts. */
    public static void main(String[] args) {
        Matrix data = FacesUtils.readFacesData();
        
        
        System.out.println(String.format(
                "Dataset has %d rows (items, faces) and %d columns (measurements per item).",
                data.getRowDimension(),  // TODO number of rows.
                data.getColumnDimension()   // TODO number of columns.
                ));

        // Prompt the user for an action.
        String action = Common.getString("action [variance/pca/project/extremes]: ");

        if ("variance".equals(action)) {
            // Plot the variance of each dimension of the dataset.
            double[] variances = variance(data);
            Common.linPlot(variances);

        } else if ("pca".equals(action)) {
            // Plot the magnitude of the components in log scale.
            PCAResult result = pca(data);
            Common.logPlot(result.values);

        } else if ("project".equals(action)) {
            // Print a row of the data projected on the new basis.
            Matrix projected = project(data);
            int id = Common.getInt("row / item: ");
            FacesUtils.printRow(projected, id);

        } else if ("extremes".equals(action)) {
            // Print items with extreme values (both min and max) along a
            // given principalcomponent.
            Matrix projected = project(data);
            int dim = Common.getInt("dimension: ");
            FacesUtils.printExtremes(projected, dim, 10);
        }
    }
}
