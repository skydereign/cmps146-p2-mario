package dk.itu.mario.level;

import java.util.Random;
import java.util.Hashtable;
import java.io.*;
import java.util.Scanner;

import dk.itu.mario.MarioInterface.Constraints;
import dk.itu.mario.MarioInterface.LevelInterface;
import dk.itu.mario.engine.sprites.SpriteTemplate;
import dk.itu.mario.engine.sprites.Enemy;
import markov.Markov;

public class RandomLevel extends Level{
//	//Store information about the level
	 public   int ENEMIES = 0; //the number of enemies the level contains
	 public   int BLOCKS_EMPTY = 0; // the number of empty blocks
	 public   int BLOCKS_COINS = 0; // the number of coin blocks
	 public   int BLOCKS_POWER = 0; // the number of power blocks
	 public   int COINS = 0; //These are the coins in boxes that Mario collect
	 public Hashtable<String, String> columns;
	 public Hashtable<String, Integer> e_change;
	 public Hashtable<String, Integer> gaps;
	 public final int maxChunkWidth = 33;
	 public final int maxChunkHeight = 18;

 
	private static Random levelSeedRandom = new Random();
//	    public static long lastSeed;
//
//	    Random random;
//
//	    private static final int ODDS_STRAIGHT = 0;
//	    private static final int ODDS_HILL_STRAIGHT = 1;
//	    private static final int ODDS_TUBES = 2;
//	    private static final int ODDS_JUMP = 3;
//	    private static final int ODDS_CANNONS = 4;
//	    
//	    private int[] odds = new int[5];
//	    
//	    private int totalOdds;
//	    
//	    private int difficulty;
//	    private int type;
//		private int gaps;
		
		public RandomLevel(int width, int height)
	    {
			super(width, height);
	    }


		public RandomLevel(int width, int height, long seed, int difficulty, int type) throws FileNotFoundException
	    {
	        this(width, height);
	        //Just going to hard code the mapping of char -> file. NOTE: THEY HAVE TO BE SINGLE CHARACTERS
	        columns = new Hashtable<String, String>();
	        columns = new Hashtable<String, String>();
	        columns.put("a", "res/columns/chunk_0.txt");
	        columns.put("b", "res/columns/chunk_1.txt");
	        columns.put("c", "res/columns/chunk_2.txt");
	        columns.put("d", "res/columns/chunk_3.txt");
	        columns.put("e", "res/columns/chunk_4.txt");
	        columns.put("f", "res/columns/chunk_5.txt");
	        columns.put("g", "res/columns/chunk_6.txt");
	        columns.put("h", "res/columns/chunk_7.txt");
	        columns.put("i", "res/columns/chunk_8.txt");
	        columns.put("j", "res/columns/chunk_9.txt");
	        columns.put("k", "res/columns/chunk_GB.txt");
	        columns.put("l", "res/columns/chunk_H.txt");
	        columns.put("m", "res/columns/chunk_J1.txt");
	        columns.put("n", "res/columns/chunk_St.txt");
	        columns.put("o", "res/columns/chunk_Tr.txt");
	        columns.put("p", "res/columns/chunk_wj.txt");
//	        columns.put("a", "res/columns/blank.txt");
//	        columns.put("a", "res/columns/blank.txt");
//	        columns.put("a", "res/columns/blank.txt");
//	        columns.put("a", "res/columns/blank.txt");
//	        columns.put("a", "res/columns/blank.txt");
//	        columns.put("a", "res/columns/blank.txt");
//	        columns.put("a", "res/columns/blank.txt");
	        
	        e_change = new Hashtable<String, Integer>();
	        e_change.put("0", 0);
	        e_change.put("1", 1);
	        e_change.put("2", 2);
	        e_change.put("3", 3);
	        e_change.put("a", -1);
	        e_change.put("b", -2);
	        e_change.put("c", -3);
	        e_change.put("d", -4);
	        e_change.put("e", -5);
	        
	        gaps = new Hashtable<String, Integer>();
	        gaps.put("0", 0);
	        gaps.put("1", 1);
	        gaps.put("2", 2);
	        gaps.put("3", 3);
	        gaps.put("4", 4);
	        gaps.put("5", 5);
	        makeLevel();
	        fixWalls();
	        
	    }
		
		
		
		public void makeLevel() throws FileNotFoundException {
	        for(int i = 0; i < 20; i++) { //starting buffer
	        	setBlock(i, height-1,  (byte) (5 + 8 * 16)); 
	        }
	        for(int i = width-40; i < width; i++) { //ending buffer
	        	setBlock(i, height-1,  (byte) (5 + 8 * 16));
	        }
	        
	        Markov levelGen = new Markov();
          levelGen.parse("mkjnaacccdddeeeadaeahhapaaaaiiaakajaaaaafagaagggaafffakak");
	        levelGen.setInitial("a");
	        
	        Markov e_changeMarkov = new Markov();
	        e_changeMarkov.parse("010101111102a020aaaa0a0a00000000000");
	        e_changeMarkov.setInitial("0");
	        
	        Markov gapsMarkov = new Markov();
	        gapsMarkov.parse("00000111111110000220330444055555000100");
	        gapsMarkov.setInitial("0");


	        
	        int xpos = 20;
	        int elevation = 2;
	        boolean calledRecently = false;
	        while(xpos < width-40) {
	        	//if(elevation >= 2 && elevation <= 10) {
	        		int[][] chunk = parseFile(columns.get(levelGen.generateNext()));
	        		xpos += applyChunk(chunk, xpos, elevation);
	        		//to fix stupid issue with fixWalls going to enforce that a gap has wait for 2 segments to be placed
	        		if(!calledRecently) {
	        			xpos += gaps.get(gapsMarkov.generateNext());
	        			calledRecently = true;
	        		} else {
	        			calledRecently = false;
	        		}
//	        	} else {
//	        		if(elevation <= 2) {
//	        			e_changeMarkov.setInitial("1");
//	        		} 
//	        		if(elevation >= 10) {
//	        			e_changeMarkov.setInitial("a");
//	        		}
//	        		xpos++;
//	        	}
	        	int delta = e_change.get(e_changeMarkov.generateNext());
	        	if(elevation + delta <= 12 && elevation + delta >= 2) {
	        		elevation += delta;
	        	} else {
	        		if(elevation + delta <= 2) {
	        			e_changeMarkov.setInitial("1");
	        		} 
	        		if(elevation + delta >= 12) {
	        			e_changeMarkov.setInitial("a");
	        		}
	        	}
	        }
	        
	        
//	        int length = 20;
//	        while(length < width-40) {
//	        	int gap = (int) Math.floor(Math.random()*5+2);
//	        	int platform = (int) Math.floor(Math.random()*16+2);
//	        	length += gap;
//	        	for(int i = 0; i < platform; i++) {
//	        		setBlock(length, height-1,  (byte) (5 + 8 * 16));
//	        		if(Math.random() < 0.1) {
//	        			setSpriteTemplate(length, height-2, new SpriteTemplate(0, false));
//	        			ENEMIES++;
//	        		}
//	        		length++;
//	        	}
//	        }
	        
	        
	        xExit = width-16;
	        yExit = height-1;
		}
		
		public int[][] parseFile(String f) throws FileNotFoundException {
			int[][] chunk = new int[maxChunkWidth][maxChunkHeight];
			Scanner in = new Scanner(new BufferedReader(new FileReader(f)));
			in.nextLine(); //passes the first line. Just gonna hardcode the keys.
			int chunkWidth = 0;
			int chunkHeight = 0;
			int i = 0;
			while(in.hasNext()) {
				//reads in a line
				//then scans that line for ints, placing them in the array.
				Scanner s = new Scanner(in.nextLine());
				int j = 0;
				while(s.hasNext()) {
					chunk[j][i] = s.nextInt();
					j++;
				}
				chunkWidth = j;
				i++;
			}
			chunkHeight = i;
			
			//putting it a properly sized array to make my life easier later. Probably wasn't necessary, but whatever
			int[][] properlySizedChunk = new int[chunkWidth][chunkHeight];
			for(int m = 0; m < chunkWidth; m++) {
				for(int n = 0; n < chunkHeight; n++) {
					properlySizedChunk[m][n] = chunk[m][n];
				}
			}

			return properlySizedChunk;
		}
		
		public int applyChunk(int[][] chunk, int width, int elevation) {
			for(int i = 0; i < chunk.length; i++) {
				int celevation = elevation;
				//int elevation = this.height - elevation; //current ypos
				//place 'ground' tiles up until surface
				//place the rest of the tiles above that
				for(int k = 0; k < elevation; k++) {
					//if(k != elevation - 1) {
						setBlock(width+i, this.height-k,  GROUND);
					//} else {
						//setBlock(width+i, this.height-k,  (byte) 129);
					//}
				}
				
				for(int j = chunk[0].length-1; j >= 0; j--) {
					if(chunk[i][j] < 256) {
						setBlock(width+i, this.height-celevation, (byte) chunk[i][j]);
						System.out.println(j + " " + chunk[i][j]);
					} else {
						if(chunk[i][j] == 256) {
							setSpriteTemplate(width+i, this.height-celevation, new SpriteTemplate(0, false));
						}
						if(chunk[i][j] == 257) {
							setSpriteTemplate(width+i, this.height-celevation, new SpriteTemplate(1, false));
						}
						if(chunk[i][j] == 258) {
							setSpriteTemplate(width+i, this.height-celevation, new SpriteTemplate(2, false));
						}
						if(chunk[i][j] == 259) {
							setSpriteTemplate(width+i, this.height-celevation, new SpriteTemplate(3, false));
						}
						if(chunk[i][j] == 262) {
							setSpriteTemplate(width+i, this.height-celevation, new SpriteTemplate(4, false));
						}
					}
					celevation++;
				}
			}
			return chunk.length;
		}

//	    public void creat(long seed, int difficulty, int type)
//	    {
//	        this.type = type;
//	        this.difficulty = difficulty;
//	        odds[ODDS_STRAIGHT] = 20;
//	        odds[ODDS_HILL_STRAIGHT] = 10;
//	        odds[ODDS_TUBES] = 2 + 1 * difficulty;
//	        odds[ODDS_JUMP] = 2 * difficulty;
//	        odds[ODDS_CANNONS] = -10 + 5 * difficulty;
//
//	        if (type != LevelInterface.TYPE_OVERGROUND)
//	        {
//	            odds[ODDS_HILL_STRAIGHT] = 0;
//	        }
//
//	        for (int i = 0; i < odds.length; i++)
//	        {
//	        	//failsafe (no negative odds)
//	            if (odds[i] < 0){
//	            	odds[i] = 0;
//	            }
//
//	            totalOdds += odds[i];
//	            odds[i] = totalOdds - odds[i];
//	        }
//
//	        lastSeed = seed;
//	        random = new Random(seed);
//
//	        //create the start location
//	        int length = 0;
//	        length += buildStraight(0, width, true);
//
//	        //create all of the medium sections
//	        while (length < width - 64)
//	        {
//	            length += buildZone(length, width - length);
//	        }
//
//	        //set the end piece
//	        int floor = height - 1 - random.nextInt(4);
//
//	        xExit = length + 8;
//	        yExit = floor;
//
//	        // fills the end piece
//	        for (int x = length; x < width; x++)
//	        {
//	            for (int y = 0; y < height; y++)
//	            {
//	                if (y >= floor)
//	                {
//	                    setBlock(x, y, GROUND);
//	                }
//	            }
//	        }
//
//	        if (type == LevelInterface.TYPE_CASTLE || type == LevelInterface.TYPE_UNDERGROUND)
//	        {
//	            int ceiling = 0;
//	            int run = 0;
//	            for (int x = 0; x < width; x++)
//	            {
//	                if (run-- <= 0 && x > 4)
//	                {
//	                    ceiling = random.nextInt(4);
//	                    run = random.nextInt(4) + 4;
//	                }
//	                for (int y = 0; y < height; y++)
//	                {
//	                    if ((x > 4 && y <= ceiling) || x < 1)
//	                    {
//	                        setBlock(x, y, GROUND);
//	                    }
//	                }
//	            }
//	        }
//
//	        fixWalls();
//
//	    }
//
//	    private int buildZone(int x, int maxLength)
//	    {
//	        int t = random.nextInt(totalOdds);
//	        int type = 0;
//
//	        for (int i = 0; i < odds.length; i++)
//	        {
//	            if (odds[i] <= t)
//	            {
//	                type = i;
//	            }
//	        }
//
//	        switch (type)
//	        {
//	            case ODDS_STRAIGHT:
//	                return buildStraight(x, maxLength, false);
//	            case ODDS_HILL_STRAIGHT:
//	                return buildHillStraight(x, maxLength);
//	            case ODDS_TUBES:
//	                return buildTubes(x, maxLength);
//	            case ODDS_JUMP:
//	            	if (gaps < Constraints.gaps)
//	            		return buildJump(x, maxLength);
//	            	else
//	            		return buildStraight(x, maxLength, false);
//	            case ODDS_CANNONS:
//	                return buildCannons(x, maxLength);
//	        }
//	        return 0;
//	    }

//	    private int buildJump(int xo, int maxLength)
//	    {	gaps++;
//	    	//jl: jump length
//	    	//js: the number of blocks that are available at either side for free
//	        int js = random.nextInt(4) + 2;
//	        int jl = random.nextInt(2) + 2;
//	        int length = js * 2 + jl;
//
//	        boolean hasStairs = random.nextInt(3) == 0;
//
//	        int floor = height - 1 - random.nextInt(4);
//	      //run from the start x position, for the whole length
//	        for (int x = xo; x < xo + length; x++)
//	        {
//	            if (x < xo + js || x > xo + length - js - 1)
//	            {
//	            	//run for all y's since we need to paint blocks upward
//	                for (int y = 0; y < height; y++)
//	                {	//paint ground up until the floor
//	                    if (y >= floor)
//	                    {
//	                        setBlock(x, y, GROUND);
//	                    }
//	                  //if it is above ground, start making stairs of rocks
//	                    else if (hasStairs)
//	                    {	//LEFT SIDE
//	                        if (x < xo + js)
//	                        { //we need to max it out and level because it wont
//	                          //paint ground correctly unless two bricks are side by side
//	                            if (y >= floor - (x - xo) + 1)
//	                            {
//	                                setBlock(x, y, ROCK);
//	                            }
//	                        }
//	                        else
//	                        { //RIGHT SIDE
//	                            if (y >= floor - ((xo + length) - x) + 2)
//	                            {
//	                                setBlock(x, y, ROCK);
//	                            }
//	                        }
//	                    }
//	                }
//	            }
//	        }
//
//	        return length;
//	    }
//
//	    private int buildCannons(int xo, int maxLength)
//	    {
//	        int length = random.nextInt(10) + 2;
//	        if (length > maxLength) length = maxLength;
//
//	        int floor = height - 1 - random.nextInt(4);
//	        int xCannon = xo + 1 + random.nextInt(4);
//	        for (int x = xo; x < xo + length; x++)
//	        {
//	            if (x > xCannon)
//	            {
//	                xCannon += 2 + random.nextInt(4);
//	            }
//	            if (xCannon == xo + length - 1) xCannon += 10;
//	            int cannonHeight = floor - random.nextInt(4) - 1;
//
//	            for (int y = 0; y < height; y++)
//	            {
//	                if (y >= floor)
//	                {
//	                    setBlock(x, y, GROUND);
//	                }
//	                else
//	                {
//	                    if (x == xCannon && y >= cannonHeight)
//	                    {
//	                        if (y == cannonHeight)
//	                        {
//	                            setBlock(x, y, (byte) (14 + 0 * 16));
//	                        }
//	                        else if (y == cannonHeight + 1)
//	                        {
//	                            setBlock(x, y, (byte) (14 + 1 * 16));
//	                        }
//	                        else
//	                        {
//	                            setBlock(x, y, (byte) (14 + 2 * 16));
//	                        }
//	                    }
//	                }
//	            }
//	        }
//
//	        return length;
//	    }
//
//	    private int buildHillStraight(int xo, int maxLength)
//	    {
//	        int length = random.nextInt(10) + 10;
//	        if (length > maxLength) length = maxLength;
//
//	        int floor = height - 1 - random.nextInt(4);
//	        for (int x = xo; x < xo + length; x++)
//	        {
//	            for (int y = 0; y < height; y++)
//	            {
//	                if (y >= floor)
//	                {
//	                    setBlock(x, y, GROUND);
//	                }
//	            }
//	        }
//
//	        addEnemyLine(xo + 1, xo + length - 1, floor - 1);
//
//	        int h = floor;
//
//	        boolean keepGoing = true;
//
//	        boolean[] occupied = new boolean[length];
//	        while (keepGoing)
//	        {
//	            h = h - 2 - random.nextInt(3);
//
//	            if (h <= 0)
//	            {
//	                keepGoing = false;
//	            }
//	            else
//	            {
//	                int l = random.nextInt(5) + 3;
//	                int xxo = random.nextInt(length - l - 2) + xo + 1;
//
//	                if (occupied[xxo - xo] || occupied[xxo - xo + l] || occupied[xxo - xo - 1] || occupied[xxo - xo + l + 1])
//	                {
//	                    keepGoing = false;
//	                }
//	                else
//	                {
//	                    occupied[xxo - xo] = true;
//	                    occupied[xxo - xo + l] = true;
//	                    addEnemyLine(xxo, xxo + l, h - 1);
//	                    if (random.nextInt(4) == 0)
//	                    {
//	                        decorate(xxo - 1, xxo + l + 1, h);
//	                        keepGoing = false;
//	                    }
//	                    for (int x = xxo; x < xxo + l; x++)
//	                    {
//	                        for (int y = h; y < floor; y++)
//	                        {
//	                            int xx = 5;
//	                            if (x == xxo) xx = 4;
//	                            if (x == xxo + l - 1) xx = 6;
//	                            int yy = 9;
//	                            if (y == h) yy = 8;
//
//	                            if (getBlock(x, y) == 0)
//	                            {
//	                                setBlock(x, y, (byte) (xx + yy * 16));
//	                            }
//	                            else
//	                            {
//	                                if (getBlock(x, y) == HILL_TOP_LEFT) setBlock(x, y, HILL_TOP_LEFT_IN);
//	                                if (getBlock(x, y) == HILL_TOP_RIGHT) setBlock(x, y, HILL_TOP_RIGHT_IN);
//	                            }
//	                        }
//	                    }
//	                }
//	            }
//	        }
//
//	        return length;
//	    }
//
//	    private void addEnemyLine(int x0, int x1, int y)
//	    {
//	        for (int x = x0; x < x1; x++)
//	        {
//	            if (random.nextInt(35) < difficulty + 1)
//	            {
//	                int type = random.nextInt(4);
//
//	                if (difficulty < 1)
//	                {
//	                    type = Enemy.ENEMY_GOOMBA;
//	                }
//	                else if (difficulty < 3)
//	                {
//	                    type = random.nextInt(3);
//	                }
//
//	                setSpriteTemplate(x, y, new SpriteTemplate(type, random.nextInt(35) < difficulty));
//	                ENEMIES++;
//	            }
//	        }
//	    }
//
//	    private int buildTubes(int xo, int maxLength)
//	    {
//	        int length = random.nextInt(10) + 5;
//	        if (length > maxLength) length = maxLength;
//
//	        int floor = height - 1 - random.nextInt(4);
//	        int xTube = xo + 1 + random.nextInt(4);
//	        int tubeHeight = floor - random.nextInt(2) - 2;
//	        for (int x = xo; x < xo + length; x++)
//	        {
//	            if (x > xTube + 1)
//	            {
//	                xTube += 3 + random.nextInt(4);
//	                tubeHeight = floor - random.nextInt(2) - 2;
//	            }
//	            if (xTube >= xo + length - 2) xTube += 10;
//
//	            if (x == xTube && random.nextInt(11) < difficulty + 1)
//	            {
//	                setSpriteTemplate(x, tubeHeight, new SpriteTemplate(Enemy.ENEMY_FLOWER, false));
//	                ENEMIES++;
//	            }
//
//	            for (int y = 0; y < height; y++)
//	            {
//	                if (y >= floor)
//	                {
//	                    setBlock(x, y,GROUND);
//
//	                }
//	                else
//	                {
//	                    if ((x == xTube || x == xTube + 1) && y >= tubeHeight)
//	                    {
//	                        int xPic = 10 + x - xTube;
//
//	                        if (y == tubeHeight)
//	                        {
//	                        	//tube top
//	                            setBlock(x, y, (byte) (xPic + 0 * 16));
//	                        }
//	                        else
//	                        {
//	                        	//tube side
//	                            setBlock(x, y, (byte) (xPic + 1 * 16));
//	                        }
//	                    }
//	                }
//	            }
//	        }
//
//	        return length;
//	    }
//
//	    private int buildStraight(int xo, int maxLength, boolean safe)
//	    {
//	        int length = random.nextInt(10) + 2;
//
//	        if (safe)
//	        	length = 10 + random.nextInt(5);
//
//	        if (length > maxLength)
//	        	length = maxLength;
//
//	        int floor = height - 1 - random.nextInt(4);
//
//	        //runs from the specified x position to the length of the segment
//	        for (int x = xo; x < xo + length; x++)
//	        {
//	            for (int y = 0; y < height; y++)
//	            {
//	                if (y >= floor)
//	                {
//	                    setBlock(x, y, GROUND);
//	                }
//	            }
//	        }
//
//	        if (!safe)
//	        {
//	            if (length > 5)
//	            {
//	                decorate(xo, xo + length, floor);
//	            }
//	        }
//
//	        return length;
//	    }
//
//	    private void decorate(int xStart, int xLength, int floor)
//	    {
//	    	//if its at the very top, just return
//	        if (floor < 1)
//	        	return;
//
//	        //        boolean coins = random.nextInt(3) == 0;
//	        boolean rocks = true;
//
//	        //add an enemy line above the box
//	        addEnemyLine(xStart + 1, xLength - 1, floor - 1);
//
//	        int s = random.nextInt(4);
//	        int e = random.nextInt(4);
//
//	        if (floor - 2 > 0){
//	            if ((xLength - 1 - e) - (xStart + 1 + s) > 1){
//	                for(int x = xStart + 1 + s; x < xLength - 1 - e; x++){
//	                    setBlock(x, floor - 2, COIN);
//	                    COINS++;
//	                }
//	            }
//	        }
//
//	        s = random.nextInt(4);
//	        e = random.nextInt(4);
//	        
//	        //this fills the set of blocks and the hidden objects inside them
//	        if (floor - 4 > 0)
//	        {
//	            if ((xLength - 1 - e) - (xStart + 1 + s) > 2)
//	            {
//	                for (int x = xStart + 1 + s; x < xLength - 1 - e; x++)
//	                {
//	                    if (rocks)
//	                    {
//	                        if (x != xStart + 1 && x != xLength - 2 && random.nextInt(3) == 0)
//	                        {
//	                            if (random.nextInt(4) == 0)
//	                            {
//	                                setBlock(x, floor - 4, BLOCK_POWERUP);
//	                                BLOCKS_POWER++;
//	                            }
//	                            else
//	                            {	//the fills a block with a hidden coin
//	                                setBlock(x, floor - 4, BLOCK_COIN);
//	                                BLOCKS_COINS++;
//	                            }
//	                        }
//	                        else if (random.nextInt(4) == 0)
//	                        {
//	                            if (random.nextInt(4) == 0)
//	                            {
//	                                setBlock(x, floor - 4, (byte) (2 + 1 * 16));
//	                            }
//	                            else
//	                            {
//	                                setBlock(x, floor - 4, (byte) (1 + 1 * 16));
//	                            }
//	                        }
//	                        else
//	                        {
//	                            setBlock(x, floor - 4, BLOCK_EMPTY);
//	                            BLOCKS_EMPTY++;
//	                        }
//	                    }
//	                }
//	            }
//	        }
//	    }
//
	    private void fixWalls()
	    {
	        boolean[][] blockMap = new boolean[width + 1][height + 1];

	        for (int x = 0; x < width + 1; x++)
	        {
	            for (int y = 0; y < height + 1; y++)
	            {
	                int blocks = 0;
	                for (int xx = x - 1; xx < x + 1; xx++)
	                {
	                    for (int yy = y - 1; yy < y + 1; yy++)
	                    {
	                        if (getBlockCapped(xx, yy) == GROUND){
	                        	blocks++;
	                        }
	                    }
	                }
	                blockMap[x][y] = blocks == 4;
	            }
	        }
	        blockify(this, blockMap, width + 1, height + 1);
	    }

	    private void blockify(Level level, boolean[][] blocks, int width, int height){
	        int to = 0;

	        boolean[][] b = new boolean[2][2];

	        for (int x = 0; x < width; x++)
	        {
	            for (int y = 0; y < height; y++)
	            {
	                for (int xx = x; xx <= x + 1; xx++)
	                {
	                    for (int yy = y; yy <= y + 1; yy++)
	                    {
	                        int _xx = xx;
	                        int _yy = yy;
	                        if (_xx < 0) _xx = 0;
	                        if (_yy < 0) _yy = 0;
	                        if (_xx > width - 1) _xx = width - 1;
	                        if (_yy > height - 1) _yy = height - 1;
	                        b[xx - x][yy - y] = blocks[_xx][_yy];
	                    }
	                }

	                if (b[0][0] == b[1][0] && b[0][1] == b[1][1])
	                {
	                    if (b[0][0] == b[0][1])
	                    {
	                        if (b[0][0])
	                        {
	                            level.setBlock(x, y, (byte) (1 + 9 * 16 + to));
	                        }
	                        else
	                        {
	                            // KEEP OLD BLOCK!
	                        }
	                    }
	                    else
	                    {
	                        if (b[0][0])
	                        {
	                        	//down grass top?
	                            level.setBlock(x, y, (byte) (1 + 10 * 16 + to));
	                        }
	                        else
	                        {
	                        	//up grass top
	                            level.setBlock(x, y, (byte) (1 + 8 * 16 + to));
	                        }
	                    }
	                }
	                else if (b[0][0] == b[0][1] && b[1][0] == b[1][1])
	                {
	                    if (b[0][0])
	                    {
	                    	//right grass top
	                        level.setBlock(x, y, (byte) (2 + 9 * 16 + to));
	                    }
	                    else
	                    {
	                    	//left grass top
	                        level.setBlock(x, y, (byte) (0 + 9 * 16 + to));
	                    }
	                }
	                else if (b[0][0] == b[1][1] && b[0][1] == b[1][0])
	                {
	                    level.setBlock(x, y, (byte) (1 + 9 * 16 + to));
	                }
	                else if (b[0][0] == b[1][0])
	                {
	                    if (b[0][0])
	                    {
	                        if (b[0][1])
	                        {
	                            level.setBlock(x, y, (byte) (3 + 10 * 16 + to));
	                        }
	                        else
	                        {
	                            level.setBlock(x, y, (byte) (3 + 11 * 16 + to));
	                        }
	                    }
	                    else
	                    {
	                        if (b[0][1])
	                        {
	                        	//right up grass top
	                            level.setBlock(x, y, (byte) (2 + 8 * 16 + to));
	                        }
	                        else
	                        {
	                        	//left up grass top
	                            level.setBlock(x, y, (byte) (0 + 8 * 16 + to));
	                        }
	                    }
	                }
	                else if (b[0][1] == b[1][1])
	                {
	                    if (b[0][1])
	                    {
	                        if (b[0][0])
	                        {
	                        	//left pocket grass
	                            level.setBlock(x, y, (byte) (3 + 9 * 16 + to));
	                        }
	                        else
	                        {
	                        	//right pocket grass
	                            level.setBlock(x, y, (byte) (3 + 8 * 16 + to));
	                        }
	                    }
	                    else
	                    {
	                        if (b[0][0])
	                        {
	                            level.setBlock(x, y, (byte) (2 + 10 * 16 + to));
	                        }
	                        else
	                        {
	                            level.setBlock(x, y, (byte) (0 + 10 * 16 + to));
	                        }
	                    }
	                }
	                else
	                {
	                    level.setBlock(x, y, (byte) (0 + 1 * 16 + to));
	                }
	            }
	        }
	    }
//	    
//	    public RandomLevel clone() throws CloneNotSupportedException {
//
//	    	RandomLevel clone=new RandomLevel(width, height);
//
//	    	clone.xExit = xExit;
//	    	clone.yExit = yExit;
//	    	byte[][] map = getMap();
//	    	SpriteTemplate[][] st = getSpriteTemplate();
//	    	
//	    	for (int i = 0; i < map.length; i++)
//	    		for (int j = 0; j < map[i].length; j++) {
//	    			clone.setBlock(i, j, map[i][j]);
//	    			clone.setSpriteTemplate(i, j, st[i][j]);
//	    	}
//	    	clone.BLOCKS_COINS = BLOCKS_COINS;
//	    	clone.BLOCKS_EMPTY = BLOCKS_EMPTY;
//	    	clone.BLOCKS_POWER = BLOCKS_POWER;
//	    	clone.ENEMIES = ENEMIES;
//	    	clone.COINS = COINS;
//	    	
//	        return clone;
//
//	      }


}
