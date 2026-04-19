package com.example.studenttask.service;

import com.example.studenttask.model.Group;
import com.example.studenttask.model.User;
import com.example.studenttask.repository.GroupRepository;
import com.example.studenttask.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class GroupService {

    private static final Logger log = LoggerFactory.getLogger(GroupService.class);

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Findet Gruppe anhand ID
     */
    public Group findById(Long groupId) {
        return groupRepository.findById(groupId).orElse(null);
    }

    /**
     * Findet alle Gruppen
     */
    public List<Group> findAll() {
        return groupRepository.findAll();
    }

    public void deleteGroup(Long id) {
        groupRepository.deleteById(id);
    }

    /**
     * Find groups by IDs
     */
    public List<Group> findAllById(List<Long> ids) {
        return groupRepository.findAllById(ids);
    }

    public Set<Group> findGroupsByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return user.get().getGroups();
        }
        return new HashSet<>();
    }

    public List<Group> getGroupsForUser(User user) {
        log.debug("Loading groups for user {} ({})", user.getId(), user.getName());
        List<Group> groups = groupRepository.findByUsersContaining(user);
        log.debug("Group repository returned {} group(s) for user {}",
                groups != null ? groups.size() : null, user.getId());
        if (log.isDebugEnabled() && groups != null) {
            for (Group group : groups) {
                log.debug("Found group for user {}: id={}, name='{}'",
                        user.getId(), group.getId(), group.getName());
            }
        }
        return groups;
    }
}
