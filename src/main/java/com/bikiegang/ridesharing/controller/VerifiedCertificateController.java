package com.bikiegang.ridesharing.controller;

import com.bikiegang.ridesharing.dao.VerifiedCertificateDao;
import com.bikiegang.ridesharing.database.Database;
import com.bikiegang.ridesharing.database.IdGenerator;
import com.bikiegang.ridesharing.parsing.Parser;
import com.bikiegang.ridesharing.pojo.CertificateDetail;
import com.bikiegang.ridesharing.pojo.RequestVerify;
import com.bikiegang.ridesharing.pojo.VerifiedCertificate;
import com.bikiegang.ridesharing.pojo.request.angel.CreateCertificateRequest;
import com.bikiegang.ridesharing.pojo.request.angel.VerifyCertificateRequest;
import com.bikiegang.ridesharing.utilities.DateTimeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hpduy17 on 7/22/15.
 */
public class VerifiedCertificateController {
    private VerifiedCertificateDao dao = new VerifiedCertificateDao();
    private Database database = Database.getInstance();

    public String createCertificate(CreateCertificateRequest request) throws JsonProcessingException {
        if (request.getRequestId() <= 0) {
            return Parser.ObjectToJSon(false, "'requestId' is invalid");
        }
        RequestVerify requestVerify = database.getRequestVerifyHashMap().get(request.getRequestId());
        if (null == requestVerify) {
            return Parser.ObjectToJSon(false, "Request does not exist");
        }
        if (request.getCertificates() == null) {
            return Parser.ObjectToJSon(false, "Certificates does not exist");
        }
        if (request.getCertificates().length == 0) {
            return Parser.ObjectToJSon(false, "Certificates is empty");
        }
        List<CertificateDetail> failCertificates = createCertificate(request.getCertificates(), requestVerify.getUserId(), requestVerify.getAngelId(), VerifiedCertificate.AVAILABLE);
        if (!failCertificates.isEmpty()) {
            return Parser.ObjectToJSon(false, "Some Certificates cannot insert", failCertificates);
        }
        return Parser.ObjectToJSon(true, "Certificates is created successfully");
    }

    public List<CertificateDetail> createCertificate(CertificateDetail[] certificateDetails, String userId, String angelId, int status) throws JsonProcessingException {
        List<CertificateDetail> failCertificates = new ArrayList<>();
        for (CertificateDetail certificateDetail : certificateDetails) {
            VerifiedCertificate certificate = new VerifiedCertificate(certificateDetail, IdGenerator.getVerifiedCertificatedId(),
                    "", DateTimeUtil.now(), userId, angelId, status);
            if (!dao.insert(certificate)) {
                failCertificates.add(certificateDetail);
            }
        }
        return failCertificates;
    }

    public String verifyCertificate(VerifyCertificateRequest request) throws JsonProcessingException {
        if (null == request.getUserId()) {
            return Parser.ObjectToJSon(false, "'userId' is invalid");
        }
        if (request.getCertificateId() <= 0) {
            return Parser.ObjectToJSon(false, "'certificateId' is invalid");
        }
        VerifiedCertificate certificate = database.getVerifiedCertificateHashMap().get(request.getCertificateId());
        if (null == certificate) {
            return Parser.ObjectToJSon(false, "Certificate does not exist");
        }
        if (certificate.getStatus() == VerifiedCertificate.AVAILABLE){
            return Parser.ObjectToJSon(false, "Certificate is verified");
        }
        if (certificate.getStatus() == VerifiedCertificate.IS_EXPIRED){
            return Parser.ObjectToJSon(false, "Certificate is expired");
        }
        certificate.setStatus(VerifiedCertificate.AVAILABLE);
        certificate.setEndorserId(request.getUserId());
        certificate.setCreatedTime(DateTimeUtil.now());
        if(dao.update(certificate))
            return Parser.ObjectToJSon(true, "Verify successfully");
        return Parser.ObjectToJSon(false, "Cannot verify this certificate");
    }


}
