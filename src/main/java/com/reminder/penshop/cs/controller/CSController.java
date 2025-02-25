package com.reminder.penshop.cs.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.reminder.penshop.admin.model.dto.TrashDTO;
import com.reminder.penshop.admin.model.service.AdminService;
import com.reminder.penshop.community.model.dto.CommentDTO;
import com.reminder.penshop.community.model.service.CommentService;
import com.reminder.penshop.community.model.service.DownloadService;
import com.reminder.penshop.cs.model.dto.InquiryDTO;
import com.reminder.penshop.cs.model.service.CSService;
import com.reminder.penshop.member.model.dto.UserImpl;
import com.reminder.penshop.member.model.service.MemberService;
import com.reminder.penshop.paging.model.dto.Criteria;
import com.reminder.penshop.paging.model.dto.PageDTO;
import com.reminder.penshop.upload.model.dto.AttachmentDTO;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

@Slf4j
@Controller
@RequestMapping("/cs")
@SessionAttributes("loginMember")
public class CSController {
	
	//썸네일 크기
	public static final int THUMB_WIDTH_SIZE = 540;
	public static final int THUMB_HEIGHT_SIZE = 540;
	
	private final CSService csService;
	private final CommentService commentService;
	private final DownloadService downloadService;
	private final MemberService memberService;
	private final AdminService adminService;
	private final MessageSource messageSource;

	@Autowired
	public CSController(CSService csService, CommentService commentService, DownloadService downloadService, MemberService memberService, AdminService adminService, MessageSource messageSource) {
		this.csService = csService;
		this.commentService = commentService;
		this.downloadService = downloadService;
		this.memberService = memberService;
		this.adminService = adminService;
		this.messageSource = messageSource;
	}
	
	/**
	 * 고객센터 메인
	 */
	@GetMapping("main")
	public void getCSPage(@Valid @ModelAttribute("criteria") Criteria criteria, String type, HttpSession session, Model model) {
		log.info("고객센터 criteria : {}", criteria);
		log.info("문의 유형 type : {}", type);
		if(type != null) { //DB에는 한글로 저장 중
			switch(type) {
			case "info": type = "회원정보"; break;
			case "product": type = "상품"; break;
			case "order": type = "주문/결제"; break;
			case "delivery": type = "배송"; break;
			case "refund": type = "교환/반품/취소"; break;
			case "reserve": type = "적립금"; break;
			case "report": type = "신고"; break;
			case "site": type = "홈페이지 이용"; break;
			}
		}
		
		String memberId = (String) session.getAttribute("loginMember");
		
		int total = 0; //페이지메이커용 전체 게시글 수
		List<InquiryDTO> myInquiryList = new LinkedList<>(); //1:1 문의 게시글 목록
		Map<Integer, Integer> totalCommentNumberMap = new HashMap<>(); //게시글별 댓글 개수
		
		if(memberId != null) {
			/* A. 일반회원은 자신이 작성한 글에 한하여 조회 가능 */
			/* B. 관리자는 모든 글 조회 가능 */
			boolean isAdmin = memberService.checkAdminOrNot(memberId) == 1 ? true : false; //관리자 확인
			if(isAdmin) memberId = "";
			
			log.info("loginMember : {}", isAdmin);
			total = csService.getTotalInquiryNumber(memberId, criteria, type);
			myInquiryList = csService.getMyInquiryList(memberId, criteria, type);
			if(myInquiryList != null) {
				for(int i=0; i < myInquiryList.size(); i++) {
					String refBoard = "문의";
					int refPostNo = myInquiryList.get(i).getInquiryNo();
					int number = commentService.getTotalCommentNumber(refBoard, refPostNo);
					totalCommentNumberMap.put(refPostNo, number);
				}
				model.addAttribute("totalCommentNumberMap", totalCommentNumberMap);
			}
		}
		model.addAttribute("total", total);
		model.addAttribute("memberInquiry", myInquiryList);
		model.addAttribute("pageMaker", new PageDTO(total, 10, criteria));
	}
	
	/**
	 * 1:1 문의 작성 및 수정
	 * : 새 글 작성 시 "/cs/main", 수정 시 "/cs/inquiry?no={}"
	 */
	@GetMapping("inquiry")
	public void getInquiryWriteForm(@RequestParam(value="no", required=false, defaultValue="0") String no,
			@AuthenticationPrincipal UserImpl user, HttpServletRequest request, Model model) {
		log.info("inquiry 작성/수정 요청");
		/* 수정 요청 구분 */
		if(!no.equals("0")) {
			int inquiryNo = Integer.parseInt(no);
			InquiryDTO originalInquiry = csService.getInquiryDetails(user.getMemberId(), inquiryNo);
			model.addAttribute("originalInquiry", originalInquiry);
			log.info("inquiry 수정 요청 확인 : {}", originalInquiry);
		}
	}
	
	@PostMapping(value="inquiry", consumes={MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	public String writeAInquiry(@ModelAttribute InquiryDTO inquiryDTO,
			@RequestParam(value="files", required=false) List<MultipartFile> files, HttpServletRequest request, RedirectAttributes rttr, Locale locale) {
		log.info("inquiry 작성/수정 요청 : {}", inquiryDTO);
		/* '작성' 또는 '수정' 요청 구분 */
		int result = 0;
		if(inquiryDTO.getInquiryNo() > 0) {
			log.info("문의 수정 : {}", inquiryDTO.getInquiryNo());
			result = csService.updateAInquiry(inquiryDTO); //기존 첨부파일 일괄 삭제 + 제목,내용,유형 수정 및 수정일 저장
		} else {
			log.info("새 문의 작성");
			result = csService.writeAInquiry(inquiryDTO);
		}
		
		/* 첨부파일 저장 */
		if(result == 1) {
			int fileCount = 0;
			int fileUploaded = 0;
			int setDownloadCount = 0;
			
			if(!files.get(0).getOriginalFilename().equals("")) {
				log.info("inquiry 첨부파일 추가 시작");
				
				String realPath = request.getSession().getServletContext().getRealPath("/");
				log.info("src/main/webapp : {}", realPath);
				
				String originalUploadPath = realPath + "upload" + File.separator + "inquiry" + File.separator + "original";
				String thumbnailUploadPath = realPath + "upload" + File.separator + "inquiry" + File.separator + "thumbnail";
				File originalDirectory = new File(originalUploadPath);
				File thumbnailDirectory = new File(thumbnailUploadPath);
				
				if(!originalDirectory.exists() || !thumbnailDirectory.exists()) { //지정 폴더가 존재하지 않을 시 생성
					originalDirectory.mkdirs(); //생성할 폴더가 하나이면 mkdir, 상위 폴더도 존재하지 않으면 한 번에 생성하란 의미로 mkdirs를 이용
					thumbnailDirectory.mkdirs();
				}
				/* 최종적으로 request를 parsing하고 파일을 저장한 뒤 필요한 내용을 담을 리스트와 맵
				 * 파일에 대한 정보는 리스트에, 다른 파라미터의 정보는 모두 맵에 담을 것임
				 * */
				Map<String, String> fileMap = new HashMap<>();
				List<Map<String, String>> fileList = new ArrayList<>();
				
				for(MultipartFile file : files) {
					UUID uuid = UUID.randomUUID(); //랜덤 문자 생성
					
					String origFileName = file.getOriginalFilename(); //원본파일명
					String extension = FilenameUtils.getExtension(origFileName); //확장자
					String randomFileName = uuid.toString().replace("-", "") + "." + extension; //랜덤파일명
					
					try {
						//원본 크기 파일을 original 폴더에 저장
						File target = new File(originalUploadPath, randomFileName);
						byte[] bytes = file.getBytes();
						FileCopyUtils.copy(bytes, target);
						
						String origFileUrl = "/upload/inquiry/original/" + uuid.toString().replace("-", "") + "." + extension;
						fileMap.put("origFileName", origFileName);
						fileMap.put("saveFileName", randomFileName);
						fileMap.put("savePath", origFileUrl);
						
						//썸네일 파일을 thumbnail 폴더에 저장
						Thumbnails.of(originalUploadPath + File.separator + randomFileName) //썸네일로 변환 후 저장
						.size(THUMB_WIDTH_SIZE, THUMB_HEIGHT_SIZE)
						.toFile(thumbnailUploadPath + File.separator + "thumbnail_" + randomFileName);
						fileMap.put("thumbnailPath", "/upload/inquiry/thumbnail/thumbnail_" + randomFileName); //웹서버에서 접근 가능한 형태로 썸네일의 저장 경로 작성
						
						fileList.add(fileMap);
						
						//현재 문의번호 조회
						int currInquiryNo;
						if(inquiryDTO.getInquiryNo() > 0) {
							currInquiryNo = inquiryDTO.getInquiryNo(); //수정 중인 번호 반영
						} else {
							currInquiryNo = csService.checkCurrInquiryNo(); //새 글 작성 중 부여된 번호 조회
						}
						
						AttachmentDTO tempFileInfo = new AttachmentDTO();
						for(int i=0; i < fileList.size(); i++) {
							tempFileInfo.setRefInqNo(currInquiryNo);
							tempFileInfo.setOrigFileName(fileList.get(i).get("origFileName"));
							tempFileInfo.setSaveFileName(fileList.get(i).get("saveFileName"));
							tempFileInfo.setSavePath(fileList.get(i).get("savePath"));
							tempFileInfo.setThumbnailPath(fileList.get(i).get("thumbnailPath"));
							
							if(i == 0) { //index 기준으로 첫번째 첨부 이미지는 메인썸네일, 그 다음은 서브썸네일에 해당
								tempFileInfo.setFileType("THUMB_MAIN");
							} else {
								tempFileInfo.setFileType("THUMB_SUB");
							}
						}
						fileUploaded = csService.attachInquiryImages(tempFileInfo); //첨부파일 업로드
						
						int currAttachNo = downloadService.checkCurrAttachNo();
						setDownloadCount += downloadService.setDownloadCount(currAttachNo); //첨부파일 기본 다운로드횟수 세팅
						
						fileCount += fileUploaded;
					} catch (IOException e) { e.printStackTrace(); }
				}
			}
			log.info("fileCount : {}", fileCount);
			log.info("setDownloadCount : {}", setDownloadCount);
			
			if(fileCount == setDownloadCount) {
				rttr.addFlashAttribute("writeInquiryMessage", messageSource.getMessage("inquiryPostedSuccessfully", null, locale));
			} else {
				rttr.addFlashAttribute("writeInquiryMessage", messageSource.getMessage("errorWhileUploadingInquiryFiles", null, locale));
			}
		} else {
			rttr.addFlashAttribute("writeInquiryMessage", messageSource.getMessage("errorWhilePostingAInquiry", null, locale));
		}
		return "redirect:/cs/main";
	}
	
	/**
	 * 1:1 문의 상세 페이지
	 */
	@GetMapping("inquiry/details")
	public void getInquiryDetails(@RequestParam("no") int inquiryNo, HttpSession session, Model model) {
		log.info("inquiry details 요청 : {}", inquiryNo);
		/* 문의 조회수 증가*/
		int count = csService.incrementInquiryViewCount(inquiryNo);
		if(count == 1) log.info("문의 조회수 + 1");
		
		String memberId = (String) session.getAttribute("loginMember");
		/* A. 일반회원은 자신이 작성한 글에 한하여 조회 가능 */
		/* B. 관리자는 모든 글 조회 가능 */
		boolean isAdmin = memberService.checkAdminOrNot(memberId) == 1 ? true : false; //관리자 확인
		if(isAdmin) memberId = "";
		
		InquiryDTO inquiryDetail =  csService.getInquiryDetails(memberId, inquiryNo); //본인확인에 필요한 memberId
		
		if(inquiryDetail == null) {
			log.info("접근권한 없는 inquiry 요청");
		} else {
			/* 댓글 조회 */
			String refBoardForComment = "문의";
			int refPostNoForComment = inquiryNo;
			List<CommentDTO> comments = commentService.getCommentsInPost(refBoardForComment, refPostNoForComment); //refBoard, refPostNo
			List<CommentDTO> nestedComments = commentService.getNestedCommentsInPost(refBoardForComment, refPostNoForComment);
			model.addAttribute("refBoard", refBoardForComment);
			model.addAttribute("refPostNo", refPostNoForComment);
			model.addAttribute("comments", comments); //댓글
			model.addAttribute("nestedComments", nestedComments); //대댓글
			
			/* 문의 상세내용 조회 */
			model.addAttribute("inquiryDetail", inquiryDetail);
		}
	}
	
	/**
	 * 문의 삭제(휴지통 이동)
	 * - 작성자는 관리자의 답변이 있기 전에 한하여 삭제 가능
	 * - 복구기한 100일 동안 '휴지통 삭제글'로 지정 및 관리되다 만료일 도래하면 자동 영구 삭제
	 */
	@GetMapping("inquiry/delete")
	public String deleteMyInquiry(@RequestParam("no") int inquiryNo, HttpSession session, RedirectAttributes rttr, Locale locale) {
		log.info("문의 삭제 요청 : {}", inquiryNo);
		String loginMember = (String) session.getAttribute("loginMember");
		InquiryDTO inquiryDTO = csService.getInquiryDetails(loginMember, inquiryNo);
		
		TrashDTO trashDTO = new TrashDTO();
		trashDTO.setRefBoard("inquiry");
		trashDTO.setRefPostNo(inquiryNo);
		trashDTO.setTrashTitle(inquiryDTO.getInquiryTitle());
		trashDTO.setTrashWriter(loginMember);
		trashDTO.setTrashDeleteBy(loginMember); //문의 삭제는 작성자 본인만 가능
		int result = adminService.moveAPostToTrash(trashDTO);
		if(result == 1) {
			rttr.addFlashAttribute("deleteInquiryMessage", messageSource.getMessage("inquiryDeletedSuccessfully", null, locale));
		} else {
			rttr.addFlashAttribute("deleteInquiryMessage", messageSource.getMessage("errorWhileDeletingAInquiry", null, locale));
		}
		return "redirect:/cs/main";
	}
}
