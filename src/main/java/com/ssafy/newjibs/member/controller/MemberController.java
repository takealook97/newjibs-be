package com.ssafy.newjibs.member.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.newjibs.member.dto.MemberDto;
import com.ssafy.newjibs.member.dto.RegisterDto;
import com.ssafy.newjibs.member.service.MemberService;
import com.ssafy.newjibs.member.service.S3Service;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/members")
public class MemberController {
	private final MemberService memberService;
	private final S3Service s3Service;

	@ApiOperation(value = "회원가입")
	@PostMapping("/register")
	public ResponseEntity<MemberDto> register(@Valid @RequestBody RegisterDto registerDto) {
		return ResponseEntity.ok(memberService.register(registerDto));
	}

	@ApiOperation(value = "프로필 사진 업로드")
	@PostMapping("/{memberId}/profile")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<Void> uploadImage(@PathVariable Long memberId,
		@RequestPart(value = "image", required = false) MultipartFile multipartFile) throws IOException {
		String url = s3Service.uploadImage(multipartFile);
		memberService.saveImageUrl(memberId, url);
		return ResponseEntity.ok().build();
	}

	@ApiOperation(value = "프로필 사진 삭제")
	@DeleteMapping("/{memberId}/profile")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<Void> deleteImage(@PathVariable Long memberId) throws IOException {
		memberService.deleteImageUrl(memberId);
		return ResponseEntity.ok().build();
	}

	@ApiOperation(value = "본인의 회원 정보를 가져온다.")
	@GetMapping("/member")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<MemberDto> getMyUserInfo(HttpServletRequest request) {
		return ResponseEntity.ok(memberService.getMyMemberWithAuthorities());
	}

	@ApiOperation(value = "특정 유저 정보를 가져온다.")
	@GetMapping("/member/{email}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<MemberDto> getUserInfo(@PathVariable String email) {
		return ResponseEntity.ok(memberService.getMemberWithAuthorities(email));
	}
}
