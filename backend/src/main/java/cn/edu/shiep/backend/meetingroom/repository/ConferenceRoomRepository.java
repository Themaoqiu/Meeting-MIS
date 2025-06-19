package cn.edu.shiep.backend.meetingroom.repository;

import cn.edu.shiep.backend.meetingroom.entity.ConferenceRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
// 数据访问接口
@Repository
public interface ConferenceRoomRepository extends JpaRepository<ConferenceRoom, Integer> {
}
