import javax.swing.*;



class RobotControl
{
   private Robot r;
   public RobotControl(Robot r)
   {
       this.r = r;
   }

   public void control(int barHeights[], int blockHeights[])
   {

	  controlMechanismForScenarioA(barHeights, blockHeights);
	 // controlMechanismForScenarioB(barHeights, blockHeights);
	 // controlMechanismForScenarioC(barHeights, blockHeights);
	 
     

   }
   
    /* ${string_prompt} --> This is needed to be copy pasted in "Run Configuration" 
    in the "Arguments" tab inside the "Program Arguments" box. To be able to edit 
    the arrays depending on each scenario */
   
	// A method to calculate the clearance
	public int maxClearance(int barHeights[], int sourceHt, int column1Ht,int column2Ht)
	{
		int maxHeight = Math.max(highestBarOrColumn(barHeights, column1Ht, column2Ht), sourceHt);
		return maxHeight;
	}

	// A method to find the highest bar
	public int highestBar(int[] barHeights) 
	{
		int maxHeight = 0;
		for (int i = 0; i < barHeights.length; i++) 
		{
			maxHeight = Math.max(maxHeight, barHeights[i]);
		}
		return maxHeight;
	}

	// A method to calculate the sum of block heights at the source
	public int sumOfBlocksInSource(int[] blockHeights) 
	{
		int sum = 0;
		for (int i = 0; i < blockHeights.length; i++) 
		{
			sum += blockHeights[i];
		}
		return sum;
	}

	// A method to find the next highest bar
	public int nextHighestBar(int barHeights[], int numOfNextBar) 
	{
		int nextHighest = barHeights[numOfNextBar];
		int maxHeight = 0;

		for (int i = 0; i < barHeights.length; i++) 
		{
			if (i + numOfNextBar < barHeights.length) 
			{
				nextHighest = barHeights[numOfNextBar + i];
				maxHeight = Math.max(maxHeight, nextHighest);
			}

		}
		return maxHeight;
	}

	// A method to find the highest column of column 1 or column 2 to use in the highestBarOrColumn method
	public int highestColumn(int column1Ht, int column2Ht) 
	{
		int maxHeight = Math.max(column1Ht, column2Ht);
		return maxHeight;
	}

	// A method to find which is higher the bar or the column to use in the clearance method
	public int highestBarOrColumn(int barHeights[], int column1Ht, int column2Ht) 
	{
		int maxHeight = Math.max(highestBar(barHeights), highestColumn(column1Ht, column2Ht));
		return maxHeight;
	}



    public void controlMechanismForScenarioA(int barHeights[], int blockHeights[])
    {     

	     int height = 2;        // Initial height of arm 1
	     int width = 1;         // Initial width of arm 2  
	     int depth = 0;         // Initial depth of arm 3

	     // Calculate the total height of the blocks
	     int sourceHt = sumOfBlocksInSource(blockHeights);

	     int blockHt = 3;
	     
	     int numOfBlocksHeight3Moved = 0;
	     
	     // Clearance is the highest height used to avoid hitting obstacles
	     int clearance;
	     
	     int column1Ht = 0;
	     int column2Ht = 0;

	     // This statement is for looping
	     for(int movedBlocks = 0; movedBlocks < blockHeights.length; movedBlocks++)
	     {
	    	
	         // Calculate the clearance
	    	 clearance = maxClearance(barHeights, sourceHt, column1Ht, column2Ht);
	    	 
	    	 // Number of blocks of height 3 = blocks moved because all the block heights are 3 in part A and B
	    	 numOfBlocksHeight3Moved = movedBlocks;
	    	 
	    	 // Raises the Height of arm 1 high enough to not crash 
		     while ( height < clearance + 1 + blockHt && height < 13 ) 
		     {
		         
		         r.up();
		         height++;
		     }
		     
		     // Bring Height down for efficiency
		     while ( height > clearance + 1 + blockHt && height < 13 )
		     {
		    	 r.down();
		    	 height--;
		     }

		     System.out.println("Debug 1: height(arm1)= "+ height + " width (arm2) = "+
		                        width + " depth (arm3) =" + depth); 

		     
		     int extendAmt = 10;		     

		     // Bring arm 2 to column 10
		     while ( width < extendAmt )
		     {
		        
		        r.extend();
		        width++;
		     }

		     System.out.println("Debug 2: height(arm1)= " + height + " width (arm2) = "+
		                        width + " depth (arm3) =" + depth); 

		     
		     // Lower the depth to reach the topmost block
		     while ( height - depth > sourceHt + 1)   
		     {
		        
		        r.lower();
		        depth++;
		     }


		     // Picking the topmost block 
		     r.pick();

		     blockHt = 3;
		     

		     // When you pick the top block, the height of the source decreases   
		     sourceHt -= blockHt;		     

		     // Raising the third arm to a certain distance so it does not collide with blocks and bars
		     while ( height - (depth + blockHt) <= nextHighestBar(barHeights, numOfBlocksHeight3Moved) 
		    		 && depth > 0 )
		     {
		         r.raise();
		         depth--;
		     } 

		     System.out.println("Debug 3: height(arm1)= " + height + " width (arm2) = "+
		                        width + " depth (arm3) =" + depth); 

		     
		     int blocksColumn = 10;
		     int firstBar = 3;
		     // Calculation to determine the contraction of the second arm
			 int contractAmt = blocksColumn - (movedBlocks + firstBar);

			 // Contracting the second arm
		     while ( contractAmt > 0 )
		     {
		         r.contract();
		         contractAmt--;
		         width--;
		     }

		     System.out.println("Debug 4: height(arm1)= " + height + " width (arm2) = "+
		                        width + " depth (arm3) =" + depth); 

		     
		     // The number of the bar used is equal to the number of the blocks moved. Only in part (A) and (B)
		     int currentBar = movedBlocks;             

		     // Lower the third arm so that the block sits just above the bar
		     while ( (height - 1) - depth - blockHt > barHeights[currentBar] )   
		     {
		         r.lower();
		         depth++;
		     }

		     System.out.println("Debug 5: height(arm1)= " + height + " width (arm2) = "+
		                        width + " depth (arm3) =" + depth); 
		     
		     // Dropping the block      
		     r.drop();

		     // The height of currentBar increases by block just placed    
		     barHeights[currentBar] += blockHt;
		     
		     // Recalculate the clearance
		     clearance = maxClearance(barHeights, sourceHt, column1Ht, column2Ht);

		     // Raising the third arm with awareness of the clearance
		     while ( height - depth <= clearance )
		     {
		         r.raise();
		         depth--;
		     }
		     System.out.println("Debug 6: height(arm1)= " + height + " width (arm2) = " +
		                        width + " depth (arm3) =" + depth); 
		     
		     
	     }
	     
	     // This just shows the message at the end of the robot program
	     JOptionPane.showMessageDialog(null,
                 "Program Part A Successful ", 
                 "Helper Code Execution", 
                 JOptionPane.INFORMATION_MESSAGE);	     
	     
    }
   
   

    public void controlMechanismForScenarioB(int barHeights[], int blockHeights[])
    {     
    	
	     int height = 2; 
         int width = 1; 
         int depth = 0; 

         int sourceHt = sumOfBlocksInSource(blockHeights);

         int blockHt = 3;
   
         int clearance;
        
         int column1Ht = 0;
         int column2Ht = 0;

         int numOfBlocksHeight3Moved = 0;
        
         // This statement is for looping
         for(int movedBlocks = 0; movedBlocks < blockHeights.length; movedBlocks++)
         {
        	
       	     // Calculate the clearance
       	     clearance = maxClearance(barHeights, sourceHt, column1Ht, column2Ht);
       	     
       	     // Number of blocks of height 3 = blocks moved because all the block heights are 3 in part A and B
       	     numOfBlocksHeight3Moved = movedBlocks;
       	 
       	     // Raises the Height of arm 1 high enough to not crash 
    	     while ( height < clearance + 1 + blockHt && height < 13 ) 
    	     {
    	         
    	         r.up();
    	         height++;
    	     }
    	     
    	     // Bring Height down for efficiency
    	     while ( height > clearance + 1 + blockHt && height < 13 )
    	     {
    	    	 r.down();
    	    	 height--;
    	     }

    	     System.out.println("Debug 1: height(arm1)= "+ height + " width (arm2) = "+
    	                        width + " depth (arm3) =" + depth); 

    	     
    	     int extendAmt = 10;		     

    	     // Bring arm 2 to column 10
    	     while ( width < extendAmt )
    	     {
    	        
    	        r.extend();
    	        width++;
    	     }

    	     System.out.println("Debug 2: height(arm1)= " + height + " width (arm2) = "+
    	                        width + " depth (arm3) =" + depth); 

    	     
    	     // Lower the depth to reach the topmost block
    	     while ( height - depth > sourceHt + 1 )   
    	     {
    	        
    	        r.lower();
    	        depth++;
    	     }


    	     // Picking the topmost block 
    	     r.pick();

    	     blockHt = 3;
    	     
    	     // When you pick the top block, the height of the source decreases   
    	     sourceHt -= blockHt;
    	     
    	     // Raising the third arm to a certain distance so it does not collide with blocks and bars
    	     while (  height - (depth + blockHt) <= nextHighestBar(barHeights, numOfBlocksHeight3Moved) && depth > 0 )
    	     {
    	         r.raise();
    	         depth--;
    	     } 

    	     System.out.println("Debug 3: height(arm1)= " + height + " width (arm2) = "+
    	                        width + " depth (arm3) =" + depth); 

    	     
    	     int blocksColumn = 10;
    	     int firstBar = 3;
    	     // Calculation to determine the contraction of the second arm
    		 int contractAmt = blocksColumn - (movedBlocks + firstBar);

    		 // Contracting the second arm
    	     while ( contractAmt > 0 )
    	     {
    	         r.contract();
    	         contractAmt--;
    	         width--;
    	     }

    	     System.out.println("Debug 4: height(arm1)= " + height + " width (arm2) = "+
    	                        width + " depth (arm3) =" + depth); 

    	     
    	     // The number of the bar used is equal to the number of the blocks moved in part (A) and (B)
    	     int currentBar = movedBlocks;             

    	     // Lower the third arm so that the block sits just above the bar
    	     while ( (height - 1) - depth - blockHt > barHeights[currentBar] )   
    	     {
    	         r.lower();
    	         depth++;
    	     }

    	     System.out.println("Debug 5: height(arm1)= " + height + " width (arm2) = "+
    	                        width + " depth (arm3) =" + depth); 
    	     
    	     // Dropping the block      
    	     r.drop();

    	     // The height of currentBar increases by block just placed    
    	     barHeights[currentBar] += blockHt;
    	     
    	     // Recalculate the clearance
    	     clearance = maxClearance(barHeights, sourceHt, column1Ht, column2Ht);

    	     // Raising the third arm with awareness of the clearance
    	     while (  height - depth <= clearance )
    	     {
    	         r.raise();
    	         depth--;
    	     }
    	     System.out.println("Debug 6: height(arm1)= " + height + " width (arm2) = " +
    	                        width + " depth (arm3) =" + depth); 
    	     
    	     
             }
        
         // This just shows the message at the end of the robot program
         JOptionPane.showMessageDialog(null,
                 "Program Part B Successful ", 
                 "Helper Code Execution", 
                 JOptionPane.INFORMATION_MESSAGE);
        }
   
    
    
    public void controlMechanismForScenarioC(int barHeights[], int blockHeights[])
    {
	    
	     int height = 2;
	     int width = 1; 
	     int depth = 0;

	     int sourceHt = sumOfBlocksInSource(blockHeights);

	     int column1Ht = 0;
	     int column2Ht = 0;
	    
	     int currentBlock = blockHeights.length - 1; 
	    	     
	     int clearance;  

	     int numOfBlocksHeight1Moved = 0;
	     int numOfBlocksHeight2Moved = 0;
	     int numOfBlocksHeight3Moved = 0;
	
	     // This statement is for looping
	     for(int movedBlocks = 0; movedBlocks < blockHeights.length; movedBlocks++)
	     {
	    	
	         int blockHt = blockHeights[currentBlock];
	    	 
	    	 int blockColumn = 0;
	    	
	    	 // Calculate the clearance
	   	     clearance = maxClearance(barHeights, sourceHt, column1Ht, column2Ht);
	   	 
	   	     // Raises the Height of arm 1 high enough to not crash 
		     while ( height < clearance + 1 + blockHt && height < 13 ) 
		     {
		         
		         r.up();
		         height++;
		     }

		     System.out.println("Debug 1: height(arm1)= "+ height + " width (arm2) = "+
		                        width + " depth (arm3) =" + depth); 

		     
		     int extendAmt = 10;		     

		     // Bring arm 2 to column 10
		     while ( width < extendAmt )
		     {
		        
		        r.extend();
		        width++;
		     }

		     System.out.println("Debug 2: height(arm1)= " + height + " width (arm2) = "+
		                        width + " depth (arm3) =" + depth); 

		     
		     // Lower the depth to reach the topmost block
		     while ( height - depth > sourceHt + 1 )   
		     {
		        
		        r.lower();
		        depth++;
		     }


		     // Picking the topmost block 
		     r.pick();
		     
		     
		     // When you pick the top block, the height of source decreases  
		     sourceHt -= blockHt;
		     
		     // Raising the third arm to a certain distance so it does not collide with blocks and bars
		     while ( height - (blockHt + 1 + depth) < highestBar(barHeights) && depth <= 0 )
		     {
		    	 
		    	 r.up();
		    	 height++;
		     } 		     
		     
		     // Raising the third arm
		     while (  height - (depth + blockHt) <= clearance && depth > 0 )
		     {
		         r.raise();
		         depth--;
		     } 

		     System.out.println("Debug 3: height(arm1)= " + height + " width (arm2) = "+
		                        width + " depth (arm3) =" + depth); 

		     
		     // This statement determines which column the block will be placed depending on its height
		     if( blockHt == 1 )
		    	 blockColumn = 1;
		     else if ( blockHt == 2 )
		    	 blockColumn = 2;
		     else if ( blockHt == 3)
		    	 // First bar is 3 then incremented by the number of the blocks of height 3 moved
		    	 blockColumn = 3 + numOfBlocksHeight3Moved;
		     
		     int numberOfColumns = 10;
		     
		     // Calculation to determine the contraction of the second arm
			 int contractAmt = numberOfColumns - blockColumn;

			 // Contracting the second arm
		     while ( contractAmt > 0 )
		     {
		         r.contract();
		         contractAmt--;
		         width--;
		     }

		     System.out.println("Debug 4: height(arm1)= " + height + " width (arm2) = "+
		                        width + " depth (arm3) =" + depth); 

		     
		     // The number of the bar used is equal to the number of the blocks of height 3 moved
		     int currentBar = numOfBlocksHeight3Moved;             

		     /* These statements find which type of block the robot is holding and to 
		     choose which column to place the block on */
		     if( blockColumn == 1 )
		     {
		    	 do
		    	 {
		    		 r.lower();
		    		 depth++;
		    	 }
		    	 while ( height - 1 - depth - blockHt > column1Ht );
		     }
		     
		     else if ( blockColumn == 2 )
		     {
		    	 do
		    	 {
		    		 r.lower();
		    		 depth++;
		    	 }
		    	 while ( height - 1 - depth - blockHt > column2Ht );
		     }
		     
		     else
		     {
		    	 do   
			     {
			         r.lower();
			         depth++;
			     }
		    	 while ( height - 1 - depth - blockHt > barHeights[currentBar] );
		     }
		     
		     System.out.println("Debug 5: height(arm1)= " + height + " width (arm2) = "+
		                        width + " depth (arm3) =" + depth); 
		     		     
		     
		     // Incrementing the number of blocks for each block type when moved
		     if( blockHt == 1 )
		    	 numOfBlocksHeight1Moved++;
		     else if( blockHt == 2 )
		    	 numOfBlocksHeight2Moved++;
		     else if( blockHt == 3 )
		    	 numOfBlocksHeight3Moved++;
		     
		     
		     // Dropping the block      
		     r.drop();

		     
		     // Calculating the columns' heights
		     if(blockHt == 1)
		    	 column1Ht += blockHt;
		     else if(blockHt == 2)
		    	 column2Ht += blockHt;
		     else if(blockHt == 3)
		    	 barHeights[currentBar] += blockHt;
		     
		     // Recalculate the clearance
		     clearance = maxClearance(barHeights, sourceHt, column1Ht, column2Ht);

		     // Raising the third arm all the way
		     while (  height - depth <= clearance )
		     {
		         r.raise();
		         depth--;
		     }
		     
		     System.out.println("Debug 6: height(arm1)= " + height + " width (arm2) = " +
		                        width + " depth (arm3) =" + depth);
		     
		     
		     // Changing the current block to the next for the next loop
		     currentBlock--;
		     		     
		     }
	    
         // This just shows the message at the end of the robot program
	     JOptionPane.showMessageDialog(null,
	             "Program Part C Successful ", 
	             "Helper Code Execution", 
	             JOptionPane.INFORMATION_MESSAGE);	     
		     
    }

} 


