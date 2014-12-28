package fiji.plugin.trackmate.tracking.oldlap.hungarian;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class JonkerVolgenantTest
{

	private int seed;

	private int pseudoRandom()
	{
		return seed = 3170425 * seed + 132102;
	}

	private double pseudoRandom( final double min, final double max )
	{
		final int random = pseudoRandom() & 0x7fffffff;
		return min + random * ( ( max - min ) / Integer.MAX_VALUE );
	}

	private double[][] generateMatrix( final int n )
	{
		final double[][] m = new double[ n ][ n ];
		for ( int j = 0; j < n; j++ )
		{
			for ( int i = 0; i < n; i++ )
			{
				m[ j ][ i ] = Math.floor( pseudoRandom( 1, 100 ) );
			}
		}
		return m;
	}

	@Test
	public void speedTest()
	{
		final MunkresKuhnAlgorithm munkresKuhn = new MunkresKuhnAlgorithm();
		final JonkerVolgenantAlgorithm jonkerVolgenant = new JonkerVolgenantAlgorithm();
		seed = 17;
		final int n = 100;
		final long start1 = System.currentTimeMillis();
		for ( int i = 0; i < n; i++ )
		{
			final double[][] weights = generateMatrix( 100 );
			munkresKuhn.computeAssignments( weights );
		}
		final long end1 = System.currentTimeMillis();
		final long start2 = System.currentTimeMillis();
		for ( int i = 0; i < n; i++ )
		{
			final double[][] weights = generateMatrix( 100 );
			jonkerVolgenant.computeAssignments( weights );
		}
		final long end2 = System.currentTimeMillis();
		System.err.println( "Munkres-Kuhn: " + ( end1 - start1 ) + "ms, Jonker-Volgenant: " + ( end2 - start2 ) + "ms" );
		assertTrue( end1 - start1 > end2 - start2 );
	}

	@Test
	public void testJonkerVolgenant()
	{
		final double[][] weights = generateMatrix( 9 );

		final MunkresKuhnAlgorithm munkresKuhn = new MunkresKuhnAlgorithm();
		final int[][] munkresKuhnResult = munkresKuhn.computeAssignments( weights );
		final JonkerVolgenantAlgorithm jonkerVolgenant = new JonkerVolgenantAlgorithm();
		final int[][] jonkerVolgenantResult = jonkerVolgenant.computeAssignments( weights );

		assertEquals( munkresKuhnResult.length, jonkerVolgenantResult.length );

		double munkresKuhnCost = 0, jonkerVolgenantCost = 0;
		for ( int i = 0; i < munkresKuhnResult.length; i++ )
		{
			munkresKuhnCost += weights[ munkresKuhnResult[ i ][ 0 ] ][ munkresKuhnResult[ i ][ 1 ] ];
		}
		for ( int i = 0; i < jonkerVolgenantResult.length; i++ )
		{
			jonkerVolgenantCost += weights[ jonkerVolgenantResult[ i ][ 0 ] ][ jonkerVolgenantResult[ i ][ 1 ] ];
		}
		assertEquals( munkresKuhnCost, jonkerVolgenantCost, 1e-5 );
	}
}
