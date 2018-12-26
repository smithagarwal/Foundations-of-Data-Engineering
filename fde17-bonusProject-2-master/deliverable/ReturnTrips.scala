import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.Column
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row

object ReturnTrips {
def compute(trips : Dataset[Row], dist : Double, spark : SparkSession) : Dataset[Row] = {

    import spark.implicits._

    val R = 6371; //radius in km
 
	def haversine(lat1:Double, lon1:Double, lat2:Double, lon2:Double)={
      val dLat=(lat2 - lat1).toRadians
      val dLon=(lon2 - lon1).toRadians
 
      val a = Math.pow(Math.sin(dLat/2),2) + Math.pow(Math.sin(dLon/2),2) * Math.cos(lat1.toRadians) * Math.cos(lat2.toRadians)
      val c = 2 * Math.asin(Math.sqrt(a))
      R * c * 1000
	}

	val trips_DF = trips.where($"pickup_longitude" =!= 0 && $"pickup_latitude" =!= 0 && $"dropoff_longitude" =!= 0 && $"dropoff_latitude" =!= 0).select('tpep_pickup_datetime,'tpep_dropoff_datetime,'pickup_longitude,'pickup_latitude,'dropoff_longitude,'dropoff_latitude);

	val dist_func = udf(haversine _)

	val interval = (dist*0.001)/111.2;

	val interval_long = (dist*0.001)/79;

	val trips_time = trips_DF.withColumn("p_time",unix_timestamp($"tpep_pickup_datetime")).withColumn("d_time",unix_timestamp($"tpep_dropoff_datetime"));

	val trips_buck = trips_time.withColumn("Bucket_ptime",floor($"p_time"/(28800))).withColumn("Bucket_dtime",floor($"d_time"/(28800))).withColumn("Bucket_plat",floor($"pickup_latitude"/interval)).withColumn("Bucket_dlat",floor($"dropoff_latitude"/interval)).withColumn("Bucket_plong",floor($"pickup_longitude"/interval_long)).withColumn("Bucket_dlong",floor($"dropoff_longitude"/interval_long));;

	val trips_buckNeighbors = trips_buck.withColumn("Bucket_ptime", explode(array($"Bucket_ptime" - 1, $"Bucket_ptime"))).withColumn("Bucket_plat", explode(array($"Bucket_plat" - 1, $"Bucket_plat", $"Bucket_plat" + 1))).withColumn("Bucket_dlat",explode(array($"Bucket_dlat" - 1, $"Bucket_dlat", $"Bucket_dlat" + 1))).withColumn("Bucket_plong", explode(array($"Bucket_plong" - 1, $"Bucket_plong", $"Bucket_plong" + 1))).withColumn("Bucket_dlong",explode(array($"Bucket_dlong" - 1, $"Bucket_dlong", $"Bucket_dlong" + 1)));

	val return_trips = trips_buck.as("a").join(trips_buckNeighbors.as("b"),
	($"b.Bucket_plat" === $"a.Bucket_dlat")
	&& ($"a.Bucket_plat" === $"b.Bucket_dlat")
	&& ($"b.Bucket_plong" === $"a.Bucket_dlong")
	&& ($"a.Bucket_plong" === $"b.Bucket_dlong")
	&& ($"a.Bucket_dtime" === $"b.Bucket_ptime") 
	&& (dist_func($"a.dropoff_latitude",$"a.dropoff_longitude",$"b.pickup_latitude",$"b.pickup_longitude") < dist)
	&& (dist_func($"b.dropoff_latitude",$"b.dropoff_longitude",$"a.pickup_latitude",$"a.pickup_longitude") < dist)
	&& ($"b.p_time" > $"a.d_time")
	&& (($"a.d_time" + 28800) > $"b.p_time"));


	return_trips
  }
}
