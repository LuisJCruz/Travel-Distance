package main;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Function;

import data_structures.ArrayList;
import interfaces.List;
/**
 * This is the Election class. 
 * @author Luis J. Cruz
 */
public class Election {
	
	private List<Candidate> candidates;
	private List<Candidate> OGcandidates;
	private List<String> elimCandidates;
	private List<Ballot> ballots;
	private List<Ballot> invBallots;
	private List<Ballot> blankBallots;
	private List<List<Integer>> totalRanks;
	private String winner;
	/**
	 * Constructor that implements the election logic using the files candidates.csv 
	 * and ballots.csv as input. (Default constructor)
	**/
	public Election() throws IOException {
	    this("candidates.csv",
	         "ballots.csv");
	}
	/**
	 * Constructor that receives the name of the candidate and ballot files and applies 
	 * the election logic. Note: The files should be found in the input folder. 
	 * @param candidates_filename Name of the file in which the candidates are written in.
	 * @param ballot_filename Name of the file in which the ballots are written in.
	**/
	public Election(String candidates_filename, String ballot_filename) throws IOException {
		String canFile = "inputFiles/"+candidates_filename;
		String ballFile = "inputFiles/"+ballot_filename;
		BufferedReader cands = new BufferedReader(new FileReader(canFile));
		BufferedReader balls = new BufferedReader(new FileReader(ballFile));
		String canInfo = "";
		String balInfo = "";
		this.candidates = new ArrayList<Candidate>();
		this.ballots = new ArrayList<Ballot>();
		this.invBallots = new ArrayList<Ballot>();
		this.blankBallots = new ArrayList<Ballot>();
		this.OGcandidates = new ArrayList<Candidate>();
		this.elimCandidates = new ArrayList<String>();
		//this while reads the candidates file and adds the candidates to a list
		while ((canInfo = cands.readLine())!=null) {
			Candidate curCan = new Candidate(canInfo);
			OGcandidates.add(curCan);
			candidates.add(curCan);
		}
		//this while reads the ballots file and adds the ballots to a list
		while ((balInfo = balls.readLine())!=null) {
			Ballot newBallot = new Ballot(balInfo, candidates);
			if (newBallot.getBallotType()==0)
				ballots.add(newBallot);
			else if (newBallot.getBallotType()==2)
				invBallots.add(newBallot);
			else if (newBallot.getBallotType()==1)
				blankBallots.add(newBallot);
	    }
		cands.close();
		balls.close();
		//this part of the code creates the table for the starting total ranks of the election
		this.totalRanks = new ArrayList<>();
		for (int i = 0; i < candidates.size(); i++) {
		    List<Integer> candidateRanks = new ArrayList<>();
		    List<Integer> ranks = new ArrayList<>();
		    for (int j = 0; j < candidates.size(); j++) {
		        candidateRanks.add(0);
		    }
		    for (int j = 0; j < ballots.size(); j++) {
		        ranks.add(0);
		    }
		    totalRanks.add(candidateRanks);
		}
		//this part of the code sets the total amount of each rank the candidates have at the beginning.
		for(Ballot ballot : ballots) {
			for (int i = 1; i < ballot.getBallotList().length; i++) {
				int update = totalRanks.get(ballot.getCandidateByRank(i)-1).get(i-1);
				totalRanks.get(ballot.getCandidateByRank(i)-1).set(i-1,update+=1);
			}
		}
		
		List<Integer> losers = new ArrayList<Integer>(); //this list stores the loser or losers of the current round
		int lowest=ballots.size(); //this variable is to save the lowest amount of a rank
		int next=0;
		//this while loop represents each round. 
		while(candidates.size()!=1) {
			for (int i = 0; i < totalRanks.size(); i++) { //this for loop iterates the first column of the total ranks, which is the #1s.
				if (totalRanks.get(i)!=null&&totalRanks.get(i).get(next)>ballots.size()*0.50) { // checks if the votes are from a eliminated candidate, if so we skip to the next candidate. We also to see if the candidate wins immediately for having more than 50% of #1s.  
					for(Candidate candidate : candidates) {
						if (candidate.getId()==i+1) {
							this.winner=candidate.getName()+"-"+totalRanks.get(candidate.getId()-1).get(0);
						}
						else {
							elimCandidates.add(candidate.getName()+"-"+totalRanks.get(i).get(0));
							for (Ballot ballot : ballots) {
								ballot.eliminate(candidate.getId());
							}
						}
					}
				}
				else if(totalRanks.get(i)!=null&&totalRanks.get(i).get(next)<lowest) { // check if the votes are from a eliminated, if so we skip to the next candidate. Sees who haves the least amounts of ones.
					lowest=totalRanks.get(i).get(next);
				}			
			}
			for (int i = 0; i < totalRanks.size(); i++) { //iterate again to see who has the least amounts of #1s and then we add their ID# to the losers list.
				if (totalRanks.get(i)!=null&&totalRanks.get(i).get(next)==lowest) {
					losers.add(i+1);
				}
			}
			if(losers.size()==1) { //if there is only one loser, add the candidate to the eliminated candidates list, eliminate the candidate from all the ballots and from the candidates list.
				for(Candidate candidate : candidates) {
					if (candidate.getId()==losers.get(0)) {
						elimCandidates.add(candidate.getName()+"-"+totalRanks.get(candidate.getId()-1).get(0));
						for (Ballot ballot : ballots) {
							ballot.eliminate(candidate.getId());
						}
						for (List<Integer> row : totalRanks) { //resets the totalRanks table for a new round
							if (row!= null) {
								for (int j = 0; j < row.size(); j++) {
					                row.set(j, 0);
					            }
					            row.remove(row.size()-1);
							}
				        }
			            totalRanks.set(losers.get(0)-1,null); // null out the index of the eliminated candidate.
						for(Ballot ballot : ballots) { //sets the new values of the ranks of the new round on the totalRanks table. 
							for (int i = 1; i < ballot.getBallotList().length; i++) {
								if(totalRanks.get(ballot.getCandidateByRank(i)-1)!=null) {
									int update = totalRanks.get(ballot.getCandidateByRank(i)-1).get(i-1);
									totalRanks.get(ballot.getCandidateByRank(i)-1).set(i-1,update+=1);
								}
							}
						}
						lowest = ballots.size();
						losers.clear();
						break; //next round
					}
				}
			}
			else { //if there is more than one loser in this round. 
				front : while(next<candidates.size()) { //check who has the lowest of the next ranks. Basically the same implementation as before. Only difference being that we iterate within the losers.
					next++;
					lowest=ballots.size();
					for (Integer loserId : losers) {
						if (totalRanks.get(loserId-1).get(next)<lowest) {
							lowest=totalRanks.get(loserId-1).get(next);
						}
					}					
					for (Integer loserId : losers) {
						if (totalRanks.get(loserId-1).get(next)!=lowest) {
							losers.remove(loserId);
						}
					}
					if(losers.size()==1) { 
						for(Candidate candidate : candidates) {
							if (candidate.getId()==losers.get(0)) {
								elimCandidates.add(candidate.getName()+"-"+totalRanks.get(candidate.getId()-1).get(0));
								for (Ballot ballot : ballots) {
									ballot.eliminate(candidate.getId());
								}
								for (List<Integer> row : totalRanks) {
									if (row!= null) {
										for (int j = 0; j < row.size(); j++) {
							                row.set(j, 0);
							            }
							            row.remove(row.size()-1); //if there is not a tie we remove the candidate from the losers.
									}
								}   
								totalRanks.set(losers.get(0)-1,null);
								for(Ballot ballot : ballots) {
									for (int i = 1; i < ballot.getBallotList().length; i++) {
										if(totalRanks.get(ballot.getCandidateByRank(i)-1)!=null) {
											int update = totalRanks.get(ballot.getCandidateByRank(i)-1).get(i-1);
											totalRanks.get(ballot.getCandidateByRank(i)-1).set(i-1,update+=1);
										}
									}
								}
								break front;//next round 
							}
						}
					}
				}
				if (next>totalRanks.size()) {//if the candidates keep being tied. we remove the candidate with the largest ID#.
					int largestId = 0;
					for (Integer loserId : losers) {
						if (loserId>=largestId) {
							largestId = loserId;
						}
					}
					for(Candidate candidate : candidates) {
						if (candidate.getId()==largestId) {
							elimCandidates.add(candidate.getName()+"-"+totalRanks.get(candidate.getId()-1).get(0));
							for (Ballot ballot : ballots) {
								ballot.eliminate(candidate.getId());
							}
							for (List<Integer> row : totalRanks) {
								if (row!= null) {
									for (int j = 0; j < row.size(); j++) {
						                row.set(j, 0);
						            }
						            row.remove(row.size()-1);
								}
					        }
							totalRanks.set(losers.get(0)-1,null);
							for(Ballot ballot : ballots) {
								for (int i = 1; i < ballot.getBallotList().length; i++) {
									if(totalRanks.get(ballot.getCandidateByRank(i)-1)!=null) {
										int update = totalRanks.get(ballot.getCandidateByRank(i)-1).get(i-1);
										totalRanks.get(ballot.getCandidateByRank(i)-1).set(i-1,update+=1);
									}
								}
							}
							break;
						}
					}
				}
				lowest=ballots.size();
				losers.clear();  //next round
				next=0;
			}
			
		}
		this.winner=candidates.get(0).getName()+"-"+totalRanks.get(candidates.get(0).getId()-1).get(0);
		electionResults();
		}
	/**
	 * @return A String of the Winner of the Election	
	 */
	public String getWinner() {
		return winner.split("-")[0];
	}
	/**
	 * @return A int of the total amount of ballots. 
	**/
	public int getTotalBallots() {
		return getTotalBlankBallots()+getTotalInvalidBallots()+getTotalValidBallots();
	}
	/**
	 * @return A int of the total amount of invalid ballots.
	 */
	public int getTotalInvalidBallots() {
		return invBallots.size();
	}
	/**
	 * @return A int of the total amount of blank ballots.
	 */
	public int getTotalBlankBallots() {
		return blankBallots.size();
	}
	/**
	 * @return A int of the total amount of valid ballots.
	 */
	public int getTotalValidBallots() {
		return ballots.size();
	}
	/**
	 * @return List of names for the eliminated candidates with the numbers of 1s they had,
	 * in order of elimination. Format is <candidate name>-<number of 1s when eliminated>
	 * 
	 */
	public List<String> getEliminatedCandidates() {
		return elimCandidates;
	} 
	/**
	 * Receives a Candidate as a parameter and returns a String of the candidates name and the current status.
	 * @param can Candidate.
	 */
	public void printCandidates(Function<Candidate, String> can) {
		for (Candidate candidate : OGcandidates) {
            System.out.println(can.apply(candidate));
        }
	}
	public int countBallots(Function<Ballot, Boolean> ball) {
		int count = 0;
        for (Ballot ballot : ballots) {
            if (ball.apply(ballot)) {
                count++;
            }
        }
        return count;
	}
	/**
	* Prints all the general information about the election as well as create the file with the election results and stores it in the input files.
	*/
	public void electionResults() {
		String outputfilename = (winner.split("-")[0]).toLowerCase().replace(" ","_");
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("outputFiles/"+outputfilename+winner.split("-")[1]+".txt"))) {
			System.out.println("Number of ballots:" + getTotalBallots());
			System.out.println("Number of blank ballots:" + getTotalBlankBallots());
			System.out.println("Number of invalid ballots:" + getTotalInvalidBallots());
			writer.write("Number of ballots: " + getTotalBallots() + "\n");
	        writer.write("Number of blank ballots: " + getTotalBlankBallots() + "\n");
	        writer.write("Number of invalid ballots: " + getTotalInvalidBallots() + "\n");
	        for (int i = 0; i < elimCandidates.size() - 1; i++) {
	        	System.out.println("Round "+(i+1)+": "+elimCandidates.get(i).split("-")[0]+" was eliminated with "+elimCandidates.get(i).split("-")[1]+" #1's");
	        	writer.write("Round " + (i + 1) + ": " + elimCandidates.get(i).split("-")[0] +" was eliminated with " + elimCandidates.get(i).split("-")[1] + " #1's\n");
	        }
	        System.out.println("Winner: "+getWinner()+" wins with "+winner.split("-")[1]+" #1's");
	        writer.write("Winner: " + getWinner() + " wins with " + winner.split("-")[1] + " #1's\n");
	        writer.close();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	}
}
