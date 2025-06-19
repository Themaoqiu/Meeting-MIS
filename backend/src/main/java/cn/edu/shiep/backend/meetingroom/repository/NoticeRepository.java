package cn.edu.shiep.backend.meetingroom.repository;

import cn.edu.shiep.backend.meetingroom.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Integer> {
}
