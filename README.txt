Hello, dear Talpa code-reviewer.

I have prepared the application which is packed as jar file to make it minim size. The app has in resources
two input files in CSV format, this format can be easy parsed. I have described there all possible locations
for the processes - all depots, all stores, all customers. The difference between data-bing.csv and
data-google.csv is in precalculated coordinates (latitude, longitude). The coordinates are very similar but
not equals. You can chose input files using spring boot profiles bing or google.
For example, "java -jar target\dron-delivery-1.0-SNAPSHOT.jar -Dspring-boot.run.profiles=bing". Without
defined profile the bing will be used. For the closest routes calculation I have implemented Dijkstra algorithm.

I have prepared the start.sh script in the root of the project. You can use its to build and start the app.
After start of the app you will see the report direct in the console. Additionally, there will be prepared
output file with the result of the calculation. It will available for analyze immediately after the app is
finished.

I have prepared some tests to be sure that the code is working as expected. They also generate output file.
It can be ignored.

Also in the root of the project you can find jpg file which demonstrates the position of location directly
on the geo map. It is useful to evaluate the solution.

Thanks,
Sergii