import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DieRollExample {

	public int roll1D6() {
		int result=0;
		Random rnd = new Random(System.currentTimeMillis());
		result = rnd.nextInt(6)+1;
		
		return result;
	}
	public int roll1D6(int seed) {
		int result=0;
		try {
			Thread.sleep(seed*2);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Random rnd = new Random(System.currentTimeMillis()+seed);
		result = rnd.nextInt(6)+1;
		
		return result;
	}
	
	public List<Integer> rollD6(int numDice) {
		//int[] results = int[numDice];
		//int[] results = new IntegerArray();
		//Integer value = new Integer(roll1D6());
		List<Integer> values = new ArrayList<>();
		Integer tempRoll = null;
		for(int i=0; i<numDice; i++) {
			int rollValue = roll1D6(i);
			//System.out.println("Rolled a "+rollValue);
			tempRoll = new Integer(rollValue);
			values.add(tempRoll);
		}
		return values;
	}
	
	public int sumOfDice(List<Integer> values) {
		int value=0;
		for (Integer temp: values) {
			value = value+temp.intValue();
		}
		return value;
	}
	
	public List<Integer> extractHighValues(List<Integer> values) {
		List<Integer> tempValues = new ArrayList<>();
		for (Integer temp: values) {
			if (temp.intValue() > 3) {
				tempValues.add(temp);
			}
		}
		return tempValues;
	}
	public List<Integer> extractLowValues(List<Integer> values) {
		List<Integer> tempValues = new ArrayList<>();
		for (Integer temp: values) {
			if (temp.intValue() <= 3) {
				tempValues.add(temp);
			}
		}
		return tempValues;
	}
	public String displayValuesAndTotal(List<Integer> values) {
		StringBuilder builder= new StringBuilder();
		builder.append("[");
		for (Integer temp: values) {
			builder.append(""+temp.intValue()+" ");
		}
		builder.append("] = "+sumOfDice(values));
		
		return builder.toString();
	}
	
	public List<Integer> combineValues(List<Integer> valuesA, List<Integer> valuesB){
		List<Integer> tempValues = new ArrayList<>();
		for (Integer tempA :  valuesA) {
			tempValues.add(tempA);
		}
		for (Integer tempB :  valuesB) {
			tempValues.add(tempB);
		}
		return tempValues;
	}
	
	public boolean rollAndTotal(int numDice) {
		boolean won = false;
		List<Integer> values = rollD6(numDice);
		List<Integer> lowValues = null;
		List<Integer> highValues = null;
		List<Integer> rerolledValues = null;	
		List<Integer> recombinedValues = null;
		System.out.println(displayValuesAndTotal(values));
		lowValues = extractLowValues(values);
		highValues = extractHighValues(values);
		//System.out.println("Low:"+displayValuesAndTotal(lowValues));
		if (highValues.size() > 17)
			System.out.println("Exceeds High Limit:"+displayValuesAndTotal(highValues));
		rerolledValues = rollD6(highValues.size());
		recombinedValues = combineValues(lowValues, rerolledValues);
		System.out.println(displayValuesAndTotal(recombinedValues)+"\n");
		if (sumOfDice(recombinedValues) < 60) {
			won = true;
		}
		return won;
	}
	
}
