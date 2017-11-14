package ix.lab05.similarity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.SequentialAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.Vector.Element;
import org.apache.mahout.math.VectorWritable;

import ix.utils.UserContribution;

/**
 * The reducer gathers all contributions of users, and computes the final
 * recommendation by summing all ratings (weighted by the similarity of each
 * user), normalizing by the sum of similarities, and outputs the final
 * recommendation.
 *
 * Input:
 *
 * [similarity: 0.8, ratings: {movie1: 5.0, movie2: 3.0}]
 * [similarity: 0.4, ratings: {movie1: 3.0, movie2: 1.0}]
 *
 * Output:
 *
 * {movie1: 4.33333, movie2: 2.3333333}
 *
 */
@SuppressWarnings("unused")
public class RecommenderReducer extends
        Reducer<NullWritable, UserContribution, NullWritable, VectorWritable> {

    private VectorWritable outputValue = new VectorWritable();

    /**
     * Computes the final recommendation by doing a weighted sum of the ratings
     * of each user.
     *
     * @param nullKey
     *            No input key is required.
     * @param userContributions
     *            The list of vectors of contributions of users, containing
     *            their ratings and similarity.
     * @param context
     *            The context of the job.
     */
	@Override
    public void reduce(NullWritable nullKey,
            Iterable<UserContribution> userContributions, Context context)
            throws IOException, InterruptedException {

    	// maps movie id to predicted rating
        Vector recommendations = new SequentialAccessSparseVector(
                Integer.MAX_VALUE, 1);
        // maps movie id to sum of similarities
        Vector normalization = new RandomAccessSparseVector(Integer.MAX_VALUE, 1);
       
        
       
        // process contribution of each user
       
        for (UserContribution userContribution : userContributions) {
          
            double similarity = userContribution.getSimilarity();
           
            if (similarity == 0) {
                continue;
            }
            //get similarity and ratings 
            Vector ratings = userContribution.getRatings();
            
            // add user contributions to sum of recommendations
            for (Iterator<Element> it = ratings.iterateNonZero(); it.hasNext();) {
                Element el = it.next();
                int movieID = el.index();
                
                double rating = el.get();
                
                double ratingTotal = recommendations.getElement(movieID).get() + rating * similarity;
                recommendations.set(movieID, ratingTotal);
                
                double similarity_sum =normalization.getElement(movieID).get() + similarity;
                //System.err.println(" sum "+similarity_sum);
          
                normalization.set(movieID, similarity_sum);
            }
          
        }
        for(Iterator<Element> it =recommendations.iterateNonZero();it.hasNext();){
        	Element el = it.next();
        	double weightedRating = el.get()/normalization.getElement(el.index()).get();
        	 recommendations.set(el.index(), weightedRating );
        }
        
  
        //TODO
        
        this.outputValue.set(recommendations);
        context.write(nullKey, this.outputValue);
    }

}