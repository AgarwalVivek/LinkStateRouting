import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;


public class Route {

	// Class variable declarations
	int[][] graph;          // topology matrix
	int distances[];		// Distances array 
	int [][]connection;		// Connection matrix 
	int temp[],tcount;				// shortest path array
    int nodeCount,source,destination,cost;	
	int deleted[];			// array of deleted elements
	
	
	/************* CREATE TOPOLOGY *****************/
	/*  Scan the file and initialize the corresponding class variables  */
	public void createTopology(String fileName)	
	{
		File file = new File(fileName);
		int i,j,count=0;
		try 
		{
			Scanner sc = new Scanner(file);
			while (sc.hasNextLine()) 	// Scan number of nodes 
			{
	            count++;				// count number of nodes
	            sc.nextLine();
	        }
			
			sc.close();
			this.nodeCount=count;
			Scanner sc2 = new Scanner(new File(fileName));
			
			// initialize class variables
			this.graph = new int[count][count];
			this.connection= new int[count][count];
			this.distances= new int[count];
			this.temp =new int[count]; 
			this.deleted = new int[count];
			this.destination=-1;
			this.source=-1;
			
			for(i=0;i<count;i++)
				this.deleted[i]=0;
			
			// Store and print topology matrix
			
			System.out.println("Review original topology matrix:");
			for(i=0;i<count;i++)
			{
				for(j=0;j<count;j++)
				{
					this.graph[i][j] = sc2.nextInt();
					System.out.print(this.graph[i][j]+"\t");
					if(this.graph[i][j]== -1 )
						this.graph[i][j]=9999;
				}
				System.out.println();
			}
	        
	    } 
	    catch (FileNotFoundException e) 
		{
	        System.out.println("Invalid file name");
	        return;
	    }
	}
	/***************** CALCULATING SHORTEST PATH ***********/
	/* Using dijkstra's algorithm 
	 * initialize arrays of distances, previous and visited
	 * run through the nodes till we find the destination and by using greedy algorithm select the node which adds up to give shortest distance from source
	 * to destination
	 */
	public void shortestPath(int graph[][],int source, int dest)
	{
		// Initialization
		int distance[] = new int[this.nodeCount];
		int previous[] = new int[this.nodeCount];
		int visited[] = new int[this.nodeCount];
		
		int i,j,min,start,d,nextNode,pcount=0,start1;
		start=0;
		for(i=0;i<this.nodeCount;i++)
		{
			distance[i]=999;
			previous[i]= -1;
			visited[i]=0;
		}

		// Starting from the source node and marking it as visited
		
		start = source;
		visited[start]=1;
		distance[start]=0;
		i=0;
		
		// Traversing through the routers
		while(visited[dest]==0)
		{
			
			min = 999;
			nextNode=0;
			for(i=0;i<this.nodeCount;i++)
			{
						
				d = distance[start] + this.graph[start][i];     // Calculates the new distance 
				if(d<distance[i] && visited[i]==0 && this.deleted[i]!= 1)	// Checks if obtained distance is less than the original and whether the node is alreafy visited and whether the selected node is deleted
				{
					distance[i]=d;								// new value will be the smaller value of distance obtained above
					previous[i]=start;							// Store the current node in the previous array to store the path
					//pcount++;
				}
				if(min>distance[i] && visited[i]==0 && this.deleted[i]!= 1)
				{
					min = distance[i];							// Check if the minimum value is greater and re-initialize the minimum value
					nextNode=i;									// set the next node
				}
				
			}
			start=nextNode;										// move to the next node
			visited[start]=1; 									// Mark the current node as visited
		}
	
		start = dest;											// set start to destination
	    j = 0;
	    start1=start;
	    
	    temp[pcount] = start;
	    pcount++;
	    while(start != source)									// Traverse till the source
	    {
	    	
	        start = previous[start];							
	        temp[pcount] = start;								// Store the values in an array which will give the path 
	 
	        pcount++;
	    }
	    temp[pcount]=start;
	    this.tcount=pcount;
	    this.cost=distance[dest];
	    
	    if(pcount>2)
		{
			this.connection[source][dest]= temp[pcount-2]+1;	// Store the values in connection array
		}
		else
		{	
			this.connection[source][dest] =start1+1;
		}
		
	}
	
	
	/************* Display Connection array of a given Router source *******************/ 
	public void displayConnection(int source)
	{
		int i;
		System.out.println("Router " + (source+1)+ " Connection Table" );
		System.out.println("Destination \t Interface");
		System.out.println("===========================");

		for (i=0; i<this.nodeCount;i++)
		{
			if(this.deleted[i] !=1)  		// Check if the router is deleted
			{
				if(i==source)
					System.out.println((i+1)+"\t --");
				else
					System.out.println((i+1)+"\t"+this.connection[source][i]);
		
			}
		}
	}
	
	
	/***************** Display the Shortest Path and The Cost *********************/
	public void displayShortest()
	{
		int i; 
		i=0;
	
		System.out.println("The shortest path from router "+ this.source+ " to "+ this.destination + "is : ");
		while((temp[i]) != (this.source-1))
		{
			System.out.print((temp[i]+1)+"<-");
			i++;
		}
		System.out.println(temp[i]+1);
		System.out.println(" the total cost is = "+this.cost);
	}
	
	
	/********************** Display PAcket route ********************************/
	
	public void displayPacket()
	{
		int i,j=0; 
		int[] temp2= new int[this.tcount];
		
		for(i=this.tcount-1; i>=0; i-- ){
			temp2[j]=temp[i];
		}
		for(i=this.tcount-1; i>=0; i-- ){
			
			for(j=0;j<this.nodeCount;j++)
			{
				if(this.deleted[j]!=1)
				{
					System.out.println(temp[i] + " " + j);
					this.shortestPath(this.graph, temp2[i], j);
				}
			}
			this.displayConnection(temp2[i]);
		}
		//System.out.println(temp[i]+1);
	
	}
	public static void main(String args[])throws IOException, NumberFormatException
	{
		Route obj = new Route();		// Initialize the object

		String scan,fileName;			
		int choice=8,flag,source,dest,i,del,j;	
		int n;
		flag=0;
		n=0;
		obj.source=obj.destination=-1;
		BufferedReader br= new BufferedReader(new InputStreamReader(System.in));  // Buffered Reader to read input from the console
		
		do{
		System.out.println("CS542 Link State Routing Simulator \n(1) Create a Network Topology \n(2) Build a Connection Table \n(3) Shortest Path to Destination Router \n(4) Modify a topology \n(5) Exit \n(6) Print all Routing table\n\n Command:");
		try{
			scan= br.readLine();					// Scan the choice of user
			choice= Integer.parseInt(scan);
		//System.out.println("Your choice :"+ choice );
		
		switch(choice)
		{
		
			case 1:
				
				System.out.println("Input original network topology matix data file:");
				scan= br.readLine();
				fileName=scan;
				//System.out.println("Filename: "+fileName);
				obj.createTopology(fileName);
				n=obj.nodeCount;
				flag=1;
				break;
		
			case 2:
				if(flag==1){
				System.out.println("Enter the source router [1-n]: ");
				scan= br.readLine();
				source = Integer.parseInt(scan);
				obj.source=source;
				
				if(obj.source>=1 && obj.deleted[obj.source-1]!=1)			// Check if value of source is a valid number
				{
					for(i=0; i<obj.nodeCount;i++)
					{
						if(obj.deleted[i]!=1)									
							obj.shortestPath(obj.graph,(obj.source-1),i);
					}
				
					obj.displayConnection(obj.source-1);
				}
				
				else
					System.out.println("Router does not exist");
				}
				else
					System.out.println("network not created");
				break;
		
			case 3:
				if(flag==1){			// Check if the topology is created
				// Accept values for source and destination
				if(obj.source==-1)
				{	
					System.out.println("Enter the source router [1-n]: ");
					scan= br.readLine();
					source=  Integer.parseInt(scan);
					obj.source=source;
				}
				
				System.out.println("Select the destination router:");
				scan= br.readLine();
				dest = Integer.parseInt(scan);
				obj.destination=dest;
				if((obj.source<=obj.nodeCount)&& (obj.destination<=obj.nodeCount) && (obj.source>0) && (obj.destination >0))   // Check if source and destination values are valid
				{
					if(obj.deleted[obj.source-1] != 1 && obj.deleted[obj.destination-1] !=1 )	// Check if source and destination values are deleted/ removed
					{	
						obj.shortestPath(obj.graph,(obj.source-1),(obj.destination-1));          // Calculate the shortest path
						obj.displayShortest();													// Display the shortest path
					}
					else if(obj.deleted[obj.source-1] == 1)						
					{
						System.out.println("Source router does not exist");
					}
					else
					{
						System.out.println("Destination does not exist");
					}
				}
				else if((obj.source>obj.nodeCount) || obj.source <0)		
				{
					System.out.println("Source does not exist");
				}
				
				else if((dest>obj.nodeCount) || (dest<0))
				{
					System.out.println("Destination does not exist");

				}
				}
				else
					System.out.println("Network not created");
				break;
				
			case 4:
				if(flag==1){						// Check if the topology is created
				System.out.println("Enter router to be removed :");
				scan = br.readLine();
				del = Integer.parseInt(scan);
				if((del)<=obj.nodeCount && del>0)
				{
					if(obj.deleted[del-1] != 1 && n>0 )
					{	
						obj.deleted[del-1]=1; 		// Set deleted value to 1 for that router
						System.out.println("Router removed");
						n--;
					}
					else
						System.out.println("Router does not exists");
				}
				else
				{
					System.out.println("Router does not exist");
				}
				
				// Accept values for source and destination
				
				if(obj.source==-1)					// if source value has not been already entered
				{	
					System.out.println("Enter the source router [1-n]: ");
					scan= br.readLine();
					source=  Integer.parseInt(scan);
					obj.source=source;
					
					
				}
				
				if(obj.source>=1 && obj.deleted[obj.source-1]!=1)			// Check if value of source is a valid number
				{
					for(i=0; i<obj.nodeCount;i++)
					{
						if(obj.deleted[i]!=1)									
							obj.shortestPath(obj.graph,(obj.source-1),i);
					}
				
					obj.displayConnection(obj.source-1);
				}
				
				else
					System.out.println("Router does not exist");
				
				if(obj.destination == -1)			// If destination value is not already entered accept it from the user 
				{
					System.out.println("Select the destination router:");
					scan= br.readLine();
					dest = Integer.parseInt(scan);
					obj.destination=dest;
				}
				
				if((obj.source<=obj.nodeCount)&& (obj.destination<=obj.nodeCount) && (obj.source>0) && (obj.destination >0))   // Check if source and destination values are valid
				{
					if(obj.deleted[obj.source-1] != 1 && obj.deleted[obj.destination-1] !=1 )	// Check if source and destination values are deleted/ removed
					{	
						obj.shortestPath(obj.graph,(obj.source-1),(obj.destination-1));          // Calculate the shortest path
						obj.displayShortest();													// Display the shortest path
					}
					else if(obj.deleted[obj.source-1] == 1)						
					{
						System.out.println("Source router does not exist");
					}
					else
					{
						System.out.println("Destination does not exist");
					}
				}
				else if((obj.source>obj.nodeCount) || obj.source <0)
				{
					System.out.println("Source does not exist");
				}
				
				else if((obj.destination>obj.nodeCount) || (obj.destination<0))
				{
					System.out.println("Destination does not exist");

				}
				}
				else
					System.out.println("Network not created");
				break;
		
			case 5:
				
				System.out.println("Exit CS542 project. Good Bye!");
				break;
			
			case 6: 
				if(flag==1){						// Check if the topology is created
				for(i=0;i<obj.nodeCount;i++)
				{
					for(j=0;j<obj.nodeCount;j++)
					{
						if(obj.deleted[i]!= 1 && obj.deleted[j] != 1)
							obj.shortestPath(obj.graph,i,j);
					}
					if(obj.deleted[i] != 1)
						obj.displayConnection(i);		// display connection table for all nodes
					else
						System.out.println("Router does not exist");

				}
				}
				else
					System.out.println("Network not created");
				break;
				
			
	/*		case 7:
				if(flag==1){			// Check if the topology is created
					// Accept values for source and destination
					if(obj.source==-1)
					{	
						System.out.println("Enter the source router [1-n]: ");
						scan= br.readLine();
						source=  Integer.parseInt(scan);
						obj.source=source;
					}
					if(obj.destination == -1)
					{
					System.out.println("Select the destination router:");
					scan= br.readLine();
					dest = Integer.parseInt(scan);
					obj.destination=dest;
					}
					if((obj.source<=obj.nodeCount)&& (obj.destination<=obj.nodeCount) && (obj.source>0) && (obj.destination >0))   // Check if source and destination values are valid
					{
						if(obj.deleted[obj.source-1] != 1 && obj.deleted[obj.destination-1] !=1 )	// Check if source and destination values are deleted/ removed
						{	
							obj.shortestPath(obj.graph,(obj.source-1),(obj.destination-1));          // Calculate the shortest path
							obj.displayPacket();													// Display the shortest path
						}
						else if(obj.deleted[obj.source-1] == 1)						
						{
							System.out.println("Source router does not exist");
						}
						else
						{
							System.out.println("Destination does not exist");
						}
					}
					else if((obj.source>obj.nodeCount) || obj.source <0)		
					{
						System.out.println("Source does not exist");
					}
					
					else if((obj.destination>obj.nodeCount) || (obj.destination<0))
					{
						System.out.println("Destination does not exist");

					}
					}
					else
						System.out.println("Network not created");
					break;
					
				*/
			default:
				System.out.println("Wrong choice");
		}
		}catch(NumberFormatException e){
			System.out.println("Invalid input. Try again");
		}
		}while(choice!=5);
	}
}
