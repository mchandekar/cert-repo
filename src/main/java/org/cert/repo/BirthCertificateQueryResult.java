package org.cert.repo;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public class BirthCertificateQueryResult {

	@Property()
    private final String key;
	
	@Property()
    private final BirthCertificate record;
	
	public BirthCertificateQueryResult(@JsonProperty("Key") final String key, @JsonProperty("Record") final BirthCertificate record) {
		this.key = key;
		this.record = record;
	}

	public String getKey() {
		return key;
	}

	public BirthCertificate getRecord() {
		return record;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getKey(), this.getRecord());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BirthCertificateQueryResult other = (BirthCertificateQueryResult) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (record == null) {
			if (other.record != null)
				return false;
		} else if (!record.equals(other.record))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BirthCertificateQueryResult [key=" + key + ", record=" + record + "]";
	}
	
}
