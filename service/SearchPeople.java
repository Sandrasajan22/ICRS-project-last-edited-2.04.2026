package com.main.icrsbackend.service;

import com.main.icrsbackend.dto.PeopleSearchResponse;
import com.main.icrsbackend.model.User;
import com.main.icrsbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchPeople {

    private final UserRepository userRepository;

    public List<PeopleSearchResponse> searchPeople(Long meId, String q) {

        List<User> users;

        if (meId != null) {
            users = userRepository.searchPeopleExcludeMe(meId, q);
        } else {
            users = userRepository.searchPeople(q);
        }

        return users.stream().map(user -> {

            String fullName = ((user.getFname() == null ? "" : user.getFname()) + " " +
                    (user.getLname() == null ? "" : user.getLname())).trim();

            String profileImg = null;
            if (user.getJobSeekerProfile() != null) {
                profileImg = user.getJobSeekerProfile().getProfilePhoto();
            } else if (user.getMentorProfile() != null) {
                profileImg = user.getMentorProfile().getProfileImage();
            } else if (user.getEmployerProfile() != null) {
                profileImg = user.getEmployerProfile().getLogo();
            } else if (user.getInstitutionProfile() != null) {
                profileImg = user.getInstitutionProfile().getLogo();
            }

            return new PeopleSearchResponse(
                    user.getId(),
                    fullName.isEmpty() ? user.getEmail() : fullName,
                    user.getEmail(),
                    user.getRole(),
                    user.isVerified(),
                    profileImg
            );

        }).toList();
    }
}