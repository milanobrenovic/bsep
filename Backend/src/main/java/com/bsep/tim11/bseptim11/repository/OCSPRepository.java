package com.bsep.tim11.bseptim11.repository;

import com.bsep.tim11.bseptim11.model.OCSPItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OCSPRepository extends JpaRepository<OCSPItem, Long> {

    OCSPItem findBySerialNumber(String serialNumber);

}
