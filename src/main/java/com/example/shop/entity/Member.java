package com.example.shop.entity;

import com.example.shop.constant.Role;
import com.example.shop.dto.MemberFormDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Getter@Setter
@ToString
@Table(name = "member")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member  extends BaseEntity{

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String address;
    
    @Enumerated(EnumType.STRING)
    private Role role;
    
    //MemberFormDTo -> member entity 변환 --> 왜? DB저장하기 위해서
    public static Member createMember(MemberFormDto memberFormDto,
                                      PasswordEncoder passwordEncoder) {

        //String password = passwordEncoder.encode(memberFormDto.getPassword());
        return Member.builder()
                .name(memberFormDto.getName())
                .email(memberFormDto.getEmail())
                .password(passwordEncoder.encode(memberFormDto.getPassword()))
                .address(memberFormDto.getAddress())
                .role(Role.ADMIN)
                .build();
    }
}
