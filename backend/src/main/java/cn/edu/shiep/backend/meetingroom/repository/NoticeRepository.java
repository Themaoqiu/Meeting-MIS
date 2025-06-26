package cn.edu.shiep.backend.meetingroom.repository;

import cn.edu.shiep.backend.meetingroom.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByRecipient_UserIdOrderByCreatedTimeDesc(Long userId);
}
