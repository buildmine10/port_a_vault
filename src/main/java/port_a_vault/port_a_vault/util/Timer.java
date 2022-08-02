package port_a_vault.port_a_vault.util;

public class Timer
{
    private long start, stop;

    public Timer() { reset(); }

    public void start() { start = System.currentTimeMillis(); }

    public long stop()
    {
        stop = System.currentTimeMillis();
        return stop - start;
    }

    public void reset()
    {
        start = 0;
        stop = 0;
    }
}
