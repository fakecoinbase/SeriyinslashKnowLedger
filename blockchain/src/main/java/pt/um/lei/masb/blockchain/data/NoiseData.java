package pt.um.lei.masb.blockchain.data;

import pt.um.lei.masb.blockchain.Sizeable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Objects;


/**
 * Ambient Noise Level measured in dB.
 */
@Entity
public final class NoiseData extends GeoData implements Sizeable {
    @Id
    @GeneratedValue
    private long id;

    @Basic(optional = false)
    private final double relativeOrRMS;

    private final double peak;

    @Basic(optional = false)
    private final NUnit unit;

    protected NoiseData() {
        super(new BigDecimal(0), new BigDecimal(0));
        relativeOrRMS = 0;
        peak = 0;
        unit = null;
    }

    public NoiseData(double rms,
                     double peak,
                     BigDecimal lat,
                     BigDecimal lng) {
        super(lat, lng);
        this.relativeOrRMS = rms;
        this.peak = peak;
        this.unit = NUnit.RMS;
    }

    public NoiseData(double relativeOrRMS,
                     BigDecimal lat,
                     BigDecimal lng) {
        super(lat, lng);
        this.relativeOrRMS = relativeOrRMS;
        this.peak = 0;
        this.unit = NUnit.DBSPL;
    }


    /**
     * @return A noise level either as an
     * RMS sampled from a PCM signal in the interval [-1, 1],
     * or a dB relative to the standard base ().
     */
    public double getNoiseLevel() {
        return this.relativeOrRMS;
    }

    /**
     * @return The peak of the PCM signal.
     */
    public double getPeakOrBase() {
        return peak;
    }

    /**
     * @return The unit for the measurement.
     */
    public NUnit getUnit() {
        return unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NoiseData noiseData = (NoiseData) o;
        return id == noiseData.id &&
                Double.compare(noiseData.relativeOrRMS, relativeOrRMS) == 0 &&
                Double.compare(noiseData.peak, peak) == 0 &&
                unit == noiseData.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, relativeOrRMS, peak, unit);
    }

    @Override
    public String toString() {
        return "NoiseData{" +
                "id=" + id +
                ", relativeOrRMS=" + relativeOrRMS +
                ", peak=" + peak +
                ", unit=" + unit +
                '}';
    }
}
