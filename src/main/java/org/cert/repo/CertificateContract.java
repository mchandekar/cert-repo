package org.cert.repo;

import java.util.UUID;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import com.owlike.genson.Genson;

@Contract(
        name = "cert-repo",
        info = @Info(
                title = "CertRepo Contract",
                description = "A Sample Certificate Repository chaincode example",
                version = "0.0.1-SNAPSHOT"))
@Default
public class CertificateContract implements ContractInterface {

	private final Genson genson = new Genson();

	private enum CertRepoErrors {
		CERT_NOT_FOUND, CERT_ALREADY_EXISTS
	}
	
	@Transaction()
    public void initLedger(final Context ctx) {
    	
        ChaincodeStub stub = ctx.getStub();
        
        BirthCertificate cert1 = new BirthCertificate("1", "Naresh", "Sharma", "01-01-2019", "Ruby Hall Clinic", "Pune");
        stub.putStringState("1", genson.serialize(cert1));
        
        BirthCertificate cert2 = new BirthCertificate("2", "Amit", "Singh", "11-08-2004", "AIMS", "Delhi");
        stub.putStringState("2", genson.serialize(cert2));
    }
	
	@Transaction()
	public BirthCertificate addNewBirthCertificate(final Context ctx, final String id, final String firstName, final String lastName, final String hospitalName, final String city, final String dateOfBirth) {
		
		ChaincodeStub stub = ctx.getStub();
		String certificateState = null;
		if(id != null) {
			certificateState = stub.getStringState(id);
			if(!certificateState.isEmpty()) {
				String errorMessage = String.format("Certificate %s already exists", id);
	            System.out.println(errorMessage);
	            throw new ChaincodeException(errorMessage, CertRepoErrors.CERT_ALREADY_EXISTS.toString());
			}
		}
		
		String randomId = generateRandomId();
		
		BirthCertificate certificate = new BirthCertificate(randomId, firstName, lastName, dateOfBirth, hospitalName, city);
		certificateState = genson.serialize(certificate);
		stub.putStringState(randomId, certificateState);
		
		return certificate;
	}
	
	@Transaction()
	public BirthCertificate queryBirthCertificateById(final Context ctx, final String id) {
		ChaincodeStub stub = ctx.getStub();
        String certificateState = stub.getStringState(id);
        
        if (certificateState.isEmpty()) {
            String errorMessage = String.format("Certificate %s does not exist - %s", id, certificateState);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, CertRepoErrors.CERT_NOT_FOUND.toString());
        }
        
        BirthCertificate certificate = genson.deserialize(certificateState, BirthCertificate.class);
        return certificate;	
	}
	
	@Transaction()
	public BirthCertificate updateBirthCertificate(final Context ctx, final String id, final String firstName, final String lastName) {
		ChaincodeStub stub = ctx.getStub();
        String certificateState = stub.getStringState(id);

        if (certificateState.isEmpty()) {
            String errorMessage = String.format("Certificate %s does not exist", id);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, CertRepoErrors.CERT_NOT_FOUND.toString());
        }
        
        BirthCertificate existingCertificate = genson.deserialize(certificateState, BirthCertificate.class);
        
        BirthCertificate newCertificate = new BirthCertificate(id, firstName, lastName, existingCertificate.getDateOfBirth(), existingCertificate.getHospitalName(), existingCertificate.getCity());
        String newCertificateState = genson.serialize(newCertificate);
        
        stub.putStringState(id, newCertificateState);
        
        return newCertificate;
	}
	
	private String generateRandomId() {
		return UUID.randomUUID().toString();
	}
}
