/**
 * An implementation of an application factory. 
 * <p>
 * This is the standard tgff-based application factory that was developed in chrisl's bachelor thesis
 * (the code is rather un-optimized und should be stripped of unnecessary classes:
 *  do we really need? -ApplicationGraph
 *                     -ApplicationGraphEditor
 *                     -DirectedEdge
 *                     -UndirectedEdge
 *                     -Vertex
 * They all seem to be something like a copy of the actual model in model elements)
 * @author moritzn
 * @see AbstractApplicationFactory  
 */

package org.tubs.epoc.SMFF.SystemFactories.ApplicationFactories.Implementations.StdApplicationFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tubs.epoc.SMFF.ModelElements.SystemModel;
import org.tubs.epoc.SMFF.ModelElements.Application.ApplicationModel;
import org.tubs.epoc.SMFF.ModelElements.Application.Task;
import org.tubs.epoc.SMFF.ModelElements.Application.TaskLink;
import org.tubs.epoc.SMFF.SystemFactories.ApplicationFactories.AbstractApplicationFactory;

/**
 * This is the standard tgff-based application factory. In order to use this factory there must be
 * a readily compiled binary of tgff (tgff or tgff.exe) in the directory of this class file.
 * The corresponding factory data can accept parameters for the old as well as the new tgff algorithm.
 * In addition to the tgff functionality this factory can also generate application models with
 * cyclic dependencies. This is achieved by using a regular tgff task graph and redirecting of the edges.
 * 
 * @author moritzn
 *
 */
public class StdTgffApplicationFactory extends AbstractApplicationFactory {
	private static Log logger = LogFactory.getLog(StdTgffApplicationFactory.class); 
	public static final String IDENTIFIER = "SystemData StdTgffApplicationFactory";

	private StdTgffApplicationFactoryData applicationFactoryData;
	private Random rnd;

	private int id;
	private int cyclic;
	private ApplicationModel app;
	// the tgff generated graph
	private ApplicationGraphEditor editor;

	/**
	 * Constructs an instance of an application factory.
	 * @param systemModel system model
	 * @param applicationFactoryData application data factory
	 */
	public StdTgffApplicationFactory(SystemModel systemModel, StdTgffApplicationFactoryData applicationFactoryData) {
		super(systemModel);
		this.applicationFactoryData = applicationFactoryData;
		this.applicationFactoryData.setApplicationFactory(this);
		this.recreateRndGens();
	}

	/**
	 * Getter method for the global identifier of this factory.
	 * @return the global identifier of this factory
	 */
	public String getIdentifier() {
		return IDENTIFIER;
	}

	/**
	 * Generates an application and returns it.
	 * @return the constructed application
	 */
	public ApplicationModel generateApplication() {
		// Application ID
		int appId = 1;
		// TGFF input and output files respectively
		File tgffopt;
		File tgff;

		// --- Creating Application Graph ---
		// empty application graph
		ApplicationGraph appGraph = new ApplicationGraph();

		// get a free application id
		for (appId = 1; appId < Integer.MAX_VALUE; appId++) {
			if (systemModel.getApplication(appId) == null) {
				break;
			}
		}

		// create an tgffopt file
		tgffopt = createTGFFOPTfile(appId);

		// compile the file
		tgff = compileTGFFOPTfile(tgffopt);

		// read out tgff output
		readoutTGFFfile(tgff, appGraph);

		// edit the application graph
		new ApplicationGraphEditor(appGraph, applicationFactoryData.isCyclicGraph(), (int) applicationFactoryData.getSeed());

		// --- Creating Application from Graph ---
		// empty global application
		ApplicationModel app = new ApplicationModel(systemModel, appId, 0);

		// --- Add Application to System Model ---
		// insert application model into system
		systemModel.addApplication(app);

		// create Tasks and TaskLinks
		createTasksAndTaskLinks(app, appGraph);

		return app;
	}

	private File createTGFFOPTfile(int appId) {
		// TGFF input file
		new File("application.tgffopt").delete();
		File tgffopt = new File("application.tgffopt");

		FileWriter w;

		try {
			// // delete file content
			// w = new FileWriter(tgffopt);
			// w.write(" ");
			// w.flush();
			// w.close();
			// write
			w = new FileWriter(tgffopt);

			// old and new algo
			int s = (int) applicationFactoryData.getSeed() + appId;
			w.write("seed " + s + '\n');

			w.write("tg_cnt " + 1 + '\n');
			w.write("task_cnt " + applicationFactoryData.getNumTasks() + " " + applicationFactoryData.getDiffNumTasks()
			    + '\n');
			w.write("task_degree " + applicationFactoryData.getTaskMaxDegrIn() + " "
			    + applicationFactoryData.getTaskMaxDegrOut() + '\n');
			w.write("period_laxity 1" + '\n');
			w.write("period_mul 1" + '\n');
			w.write("prob_periodic 1.0" + '\n');

			// only new algo
			if (applicationFactoryData.isNewAlgorithm()) {
				w.write("" + '\n');
				w.write("gen_series_parallel true" + '\n');
				w.write("" + '\n');
				if (applicationFactoryData.isMustRejoin()) {
					w.write("series_must_rejoin true" + '\n');
				} else {
					w.write("series_must_rejoin false" + '\n');
				}
				w.write("series_subgraph_fork_out " + applicationFactoryData.getForkOut() + '\n');
				w.write("series_len " + applicationFactoryData.getSeriesLen() + " " + applicationFactoryData.getDiffSeriesLen()
				    + "\n");
				w.write("series_wid " + applicationFactoryData.getParallelSeries() + " "
				    + applicationFactoryData.getDiffParallelSeries() + "\n");
			}

			w.write("tg_write" + '\n');

			w.flush();
			w.close();
		} catch (IOException e) {
			logger.error("Error while editing application.tgffopt", e);
		}
		return tgffopt;
	}

	private static File compileTGFFOPTfile(File tgffopt) {
		// check for existence of tgffoptfile
		if (tgffopt == null) {
			JOptionPane.showMessageDialog(null, "Error, No such file!");
			return null;
		} else {
			// outputfilename and -file
			String outFilename;
			File outFile;

			StringTokenizer st = new StringTokenizer(tgffopt.getName(), ".");

			outFilename = st.nextToken();

			new File(outFilename + ".tgff").delete();

			try {
				// test if the os is windows or unix
				String os = System.getProperty("os.name");
				logger.info(os);

				Process p;
				String inputString;
        String tgff;
        if(os.contains("Windows")){
          tgff = StdTgffApplicationFactory.class.getResource("tgff.exe").toString();
				if (tgff.startsWith("file:/")) {
					tgff = tgff.substring(6);
				}
					inputString = tgff + " " + outFilename;
          System.out.println(inputString);
        }
        else{
          tgff = StdTgffApplicationFactory.class.getResource("tgff").toString();
          if(tgff.startsWith("file:/")){
            tgff = tgff.substring(5);
          }
					inputString = tgff + " " + outFilename;
				}

				// execute tgff and wait for it to finish
				p = Runtime.getRuntime().exec(inputString);
				p.waitFor();

				// create outputfile
				outFile = new File(outFilename + ".tgff");

				return outFile;
			} catch (Exception e) {
				logger.error(e);
				return null;
			}
		}
	}

	// 4) read out the tgff file
	private static void readoutTGFFfile(File tgff, ApplicationGraph appGraph) {
		BufferedReader buff;
		String line;

		try {
			buff = null;
			buff = new BufferedReader(new FileReader(tgff));

			int vertexIndex = 0;
			while ((line = buff.readLine()) != null) {
				// read out TaskCells names and Ids

				if (!line.contains("@") && line.contains("TASK")) {
					// cell's name
					String name;
					// string array contains all attributes of a line
					// split by whitespace
					String[] arguments = line.split("\\s+");

					// the second contains cell's name
					name = arguments[2];
					Vertex vertex = new Vertex(name, vertexIndex);
					// add vertex to appGr
					appGraph.insertVertex(vertex);
					vertexIndex++;
				}

				// read out TaskLinks
				if (line.contains("ARC")) {
					// links's name
					String name = null;
					String src = null;
					String trg = null;
					// simple counter
					int i = 0;
					String[] arguments = line.split("\\s+");
					for (String argument : arguments) {
						switch (i) {
						case 2:
							name = argument;
						case 4:
							src = argument;
						case 6:
							trg = argument;
						}
						i++;
					}

					Vertex m = null;
					Vertex n = null;

					for (int k = 0; k < appGraph.getVertices().size(); k++) {

						if (src.equals(appGraph.getVertices().get(k).getName()))
							n = appGraph.getVertices().get(k);
						if (trg.equals(appGraph.getVertices().get(k).getName()))
							m = appGraph.getVertices().get(k);
					}

					UndirectedEdge vedge = new UndirectedEdge(name, n, m);
					// add undirected edge to appGraph
					appGraph.insertEdge(vedge);
				}

			}
		} catch (IOException e) {
			logger.error("Error while reading application.tgff", e);
		}
	}

	/**
	 * create the Tasks and TaskLinks
	 */
	private void createTasksAndTaskLinks(ApplicationModel app, ApplicationGraph appGraph) {
		logger.info("model(V,ID): " + app.getAppV() + " " + app.getAppId());

		// Tasks
		for (int i = 0; i < appGraph.getVertices().size(); i++) {

			Task task = new Task(appGraph.getVertices().get(i).getName(), // name
			    app, // application
			    appGraph.getVertices().get(i).getID()); // task id
			app.addTask(task);
			logger.info(task.getUniqueName());
		}
		// TaskLinks
		for (int i = 0; i < appGraph.getDirectedEdges().size(); i++) {
			logger.info("model(V,ID): " + app.getAppV() + " " + app.getAppId());

			TaskLink link = new TaskLink(appGraph.getDirectedEdges().get(i).getName(), // name
			    app, // application
			    appGraph.getDirectedEdges().get(i).getId(), // link id
			    appGraph.getDirectedEdges().get(i).getSource().getID(), // source task id
			    appGraph.getDirectedEdges().get(i).getDestination().getID()); // target task id
			logger.info(link.getUniqueName() + "name:" + link.getShortName());
			app.addTaskLink(link);
			// add TaskLink to Tasks
			systemModel.getApplication(app.getAppId()).getTask(appGraph.getDirectedEdges().get(i).getSource().getID())
			    .addTaskLink(link);
			systemModel.getApplication(app.getAppId()).getTask(appGraph.getDirectedEdges().get(i).getDestination().getID())
			    .addTaskLink(link);
		}

		// link the application graph in the application model
		this.setAppGraph(app, appGraph);
	}

	/**
	 * Stores the ApplicationGraph in the ApplicationModel.
	 * 
	 * @param globalApp global application
	 * @param appGraph application graph
	 */
	private void setAppGraph(ApplicationModel globalApp, ApplicationGraph appGraph) {
		// set in global app
		globalApp.addAppData(appGraph, false, false, false);
	}

	/**
	 * Getter method for the global application
	 * @return the global application
	 */
	public ApplicationModel getGlobalApplication() {
		return app;
	}

	/**
	 * Getter method for the application factory data
	 * @return the application factory data of this application factory
	 */
	public StdTgffApplicationFactoryData getApplicationFactoryData() {
		return applicationFactoryData;
	}

	/**
	 * Seed the random number generator via the seed of the application factory data.
	 */
	public void recreateRndGens() {
		rnd = new Random(applicationFactoryData.getSeed());
	}
}
