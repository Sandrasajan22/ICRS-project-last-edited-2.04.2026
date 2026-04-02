
package com.main.icrsbackend.service.feed;

import com.main.icrsbackend.model.User;
import com.main.icrsbackend.model.jobseeker.JobSeekerProfile;
import org.springframework.stereotype.Component;

@Component
public class FeedUserMapper {

    public String getDisplayName(User user) {
        String fname = user.getFname() == null ? "" : user.getFname().trim();
        String lname = user.getLname() == null ? "" : user.getLname().trim();

        String fullName = (fname + " " + lname).trim();

        if (!fullName.isBlank()) return fullName;
        if (!fname.isBlank()) return fname;
        return user.getEmail();
    }

    public String getFirstName(User user) {
        String fname = user.getFname() == null ? "" : user.getFname().trim();
        if (!fname.isBlank()) return fname;

        String lname = user.getLname() == null ? "" : user.getLname().trim();
        if (!lname.isBlank()) return lname;

        return user.getEmail();
    }

    public String getProfileImage(User user) {
        JobSeekerProfile profile = user.getJobSeekerProfile();
        if (profile == null) return "";

        if (profile.getProfilePhoto() != null && !profile.getProfilePhoto().trim().isEmpty()) {
            return profile.getProfilePhoto().trim();
        }

        return "";
    }
}