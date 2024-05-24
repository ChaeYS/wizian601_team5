package com.wizian.web.controller;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.wizian.web.dto.AdminDTO;
import com.wizian.web.dto.BoardDTO;
import com.wizian.web.dto.PfRsvDTO;
import com.wizian.web.dto.EcounAdDTO;
import com.wizian.web.service.AdminService;

@Controller
public class AdminController {
	
	 //private final AdminService service;

		/*
		 * @Autowired public AdminController(AdminService service) { this.service =
		 * service; }
		 */
	    
	@Autowired
	private AdminService adminService;
	
	@GetMapping("/admin/main")
	public String mypage() {
		
		return "admin/main";
	}
	
	@GetMapping("/admin/gcoun")
	public String gcoun(Model model) {
		
	List<Map<String, Object>> getGcounCslList = adminService.getGcounCslList();
	model.addAttribute("cslList" ,getGcounCslList);
		
		
		return "admin/gcoun";
	}
	
	@GetMapping("/admin/student")
	public String student() {
		
		
		return "/admin/student";
	}
	
	@ResponseBody
	@GetMapping("/admin/studentList")
	public List<Map<String, Object>> readData() {
		List<Map<String, Object>> studentList = adminService.studentList();
		return studentList;
	}
	
	@GetMapping("/admin/counselor")
	public String counselor() {
		
		return "/admin/counselor";
	}
	
	@GetMapping("/admin/professor")
	public String professor() {
		return "/admin/professor";
	}
	
	//지도교수 상담
	@GetMapping("/admin/pfcoun")
	public String pfcoun() {
		return "/admin/pfcoun";
	}
	
	@ResponseBody
	@PostMapping("/admin/pfcounEnroll")
	public int pfcounEnroll(
		@RequestParam("PF_NO") String pfNo,
		@RequestParam("STUD_NO") String studNo,
		@RequestParam("PF_CONTENTS") String pfContents,
		@RequestParam("PF_COUN_DT") String pfcounDT,
		@RequestParam("PF_COMMENT") String pfComm,
		@RequestParam("PF_COUN_STATE_CD") String pfStCD){
		//System.out.println(pfNo);
		//System.out.println(studNo);
		//System.out.println(pfContents);
		//System.out.println(pfcounDT);
		//System.out.println(pfComm);
		//System.out.println(pfStCD);
		
		//date 타입으로 변환
		String[] dateTime = pfcounDT.split("T");
		String dateStr = dateTime[0];
		System.out.println(dateStr);
		String time = dateTime[1].substring(0,2);
		System.out.println(time);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = dateFormat.parse(dateStr);
			System.out.println(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		//DTO에 저장
		PfRsvDTO pfRsv = new PfRsvDTO();
		pfRsv.setPF_NO(pfNo);
		pfRsv.setSTUD_NO(studNo);
		pfRsv.setPF_CONTENTS(pfContents);
		pfRsv.setPF_COUN_RSVT_YMD(date);
		pfRsv.setPF_COUN_RSVT_TIME(time);
		pfRsv.setPF_COUN_YMD(date);
		pfRsv.setPF_COUN_TIME_CD(time);
		pfRsv.setPF_COUN_COMMENT(pfComm);
		pfRsv.setPF_COUN_STATE_CD(pfStCD);
		
		int pfCounResult = adminService.pfCounEnroll(pfRsv);
		if (pfCounResult==1) { System.out.println("성공");
		} else {System.out.println("실패");}
		//메소드 실행
		
		
		
		return pfCounResult;
	}
	
	@ResponseBody
	@GetMapping("/admin/getPfcounList")
	public List<Map<String, Object>> getPfcounList() {
		List<Map<String, Object>> getPfcounList = adminService.getPfcounList();
		return getPfcounList;
	}
	
	@ResponseBody
	@PostMapping("/admin/pfcounModify")
	public void pfcounModify(
		@RequestParam("counNum") int counNum,
        @RequestParam(value = "fieldName", required = false) String fieldName,
        @RequestParam(value = "fieldValue", required = false) String fieldValue) {
		Map<String, Object> map = new HashMap<>();
			System.out.println(fieldName);
			System.out.println(fieldValue);
		if (fieldName.equals("PF_COUN_COMMENT")) {
			map.put("counNum", counNum);
			map.put("fieldValue", fieldValue);
			adminService.pfCmtUpdate(map);
			//System.out.println("삽입성공1");
		}else if(fieldName.equals("PF_COUN_YMD")) {
			map.put("counNum", counNum);
			map.put("fieldValue", fieldValue);
			adminService.pfCounDateUpdate(map);
			//System.out.println("삽입성공2");
		}else if(fieldName.equals("PF_COUN_TIME_CD")) {
			map.put("counNum", counNum);
			map.put("fieldValue", fieldValue);
			adminService.pfCounTimeUpdate(map);
			//System.out.println("삽입성공3");
		}else {
			//EMP_STTS_CD
			map.put("counNum", counNum);
			map.put("fieldValue", fieldValue);
			adminService.pfStateUpdate(map);
			//System.out.println("삽입성공4");
		}
	}
	
	
	@GetMapping("/admin/counselorList")
	public List<Map<String, Object>> counselorList(){
		
		List<Map<String, Object>> counselorList = adminService.counselorList();
		return counselorList;
	}
	
	@ResponseBody
	@PostMapping("/admin/getGcounStudList")
	public List<Map<String, Object>> getGcounStudList(@RequestParam("gcounCd") String gcounCd) {
		
		List<Map<String, Object>> getGcounStudList = adminService.getGcounStudList(gcounCd);
		return getGcounStudList;
	}
	
	@ResponseBody
	@GetMapping("/admin/getGcounList")
	public List<Map<String, Object>> getGcounList() {
		List<Map<String, Object>> getGcounList = adminService.getGcounList();
		
		return getGcounList;
	}
	
	// 취업상담
	@GetMapping("/admin/ecoun")
	public String ecoun() {		
		return "admin/ecoun";
	}
	
	//개인상담
	@GetMapping("/admin/indicoun")
	public String indicoun() {	
		return "admin/indicoun";
	}
	
	
	@ResponseBody
	@GetMapping("/admin/getEcounList")
	public List<Map<String, Object>> getEcounList() {
		List<Map<String, Object>> getEcounList = adminService.getEcounList();
		System.out.println(getEcounList);
		
		return getEcounList;
	}
	
	
	//개인상담 보드리스트 
	@ResponseBody
    @GetMapping("/admin/boardList")
    public List<Map<String, Object>> getBoardList(@RequestParam("studentNo") String studentNo) {
        List<Map<String, Object>> boardList = adminService.getBoardListByStudentNo(studentNo);
        return boardList;
    }
	
	
	
	
	//개인상담 포스트디테일
	 @ResponseBody
	    @GetMapping("/admin/postDetail")
	    public Map<String, Object> getPostDetail(@RequestParam("postId") int postId) {
	        Map<String, Object> response = new HashMap<>();
	        BoardDTO detail = adminService.getPostDetail(postId);
	        List<BoardDTO> replies = adminService.getReplies(postId);

	        // BoardDTO를 Map으로 변환
	        Map<String, Object> detailMap = new HashMap<>();
	        detailMap.put("PST_SN", detail.getPST_SN());
	        detailMap.put("PST_CAT", detail.getPST_CAT());
	        detailMap.put("PST_TTL", detail.getPST_TTL());
	        detailMap.put("PST_CN", detail.getPST_CN());
	        detailMap.put("WRITER", detail.getWRITER());
	        detailMap.put("PSTG_YMD", detail.getPSTG_YMD());
	        detailMap.put("MDFCN_YMD", detail.getMDFCN_YMD());
	        detailMap.put("PST_COMP", detail.getPST_COMP());
	        detailMap.put("BBS_SN", detail.getBBS_SN());

	        response.put("detail", detailMap);
	        response.put("replies", replies);
	        return response;
	    }
	
	
	
	 @GetMapping("/admin/incompleteConsultCount")
	 public ResponseEntity<Integer> countIncompletePostsByStudentNo(@RequestParam("studentNo") String studentNo) {
	     int count = adminService.getIncompleteConsultCount(studentNo);
	     return ResponseEntity.ok(count);
	 }
	 
	 
	 // 취업 상담
	@ResponseBody
	@PostMapping("/admin/getEcounStudList")
	public List<Map<String, Object>> getEcounStudList(@RequestParam("cslNo") String cslNo) {
		
		List<Map<String, Object>> getEcounStudList = adminService.getEcounStudList(cslNo);
		return getEcounStudList;
	}
	
	@ResponseBody
	@PostMapping("/admin/getEcounStudModify")
	public void getEcounStudModify(@RequestParam("counNum") int counNum,
            @RequestParam(value = "fieldName", required = false) String fieldName,
            @RequestParam(value = "fieldValue", required = false) String fieldValue) {

		
		Map<String, Object> map = new HashMap<>();
		
		if (fieldName.equals("COUN_CN")) {
			map.put("counNum", counNum);
			map.put("fieldValue", fieldValue);
			adminService.updateCounCn(map);
			//System.out.println("삽입성공1");
		}else if(fieldName.equals("EMP_COUN_YMD")) {
			map.put("counNum", counNum);
			map.put("fieldValue", fieldValue);
			adminService.updateCounYmd(map);
			//System.out.println("삽입성공2");
		}else if(fieldName.equals("EMP_COUN_CD")) {
			map.put("counNum", counNum);
			map.put("fieldValue", fieldValue);
			adminService.updateCounCd(map);
			//System.out.println("삽입성공3");
		}else {
			//EMP_STTS_CD
			map.put("counNum", counNum);
			map.put("fieldValue", fieldValue);
			adminService.updateSttsCd(map);
			//System.out.println("삽입성공4");
		}
	}

	//System.out.println(counNum);
	//System.out.println(fieldName);
	//System.out.println(fieldValue);
	
//	@ResponseBody
//	@PostMapping("/admin/gcounEnroll")
//	public int gcounEnroll(AdminDTO adminDTO) {
//		System.out.println("컨트롤러 실행");
//		int gcounEnroll = adminService.gcounEnroll(adminDTO);
//		
//		return gcounEnroll;
//	}
	
	@ResponseBody
	@PostMapping("/admin/gcounEnroll")
	public int gcounEnroll( @RequestParam("CSL_NO") String cslNo,
            @RequestParam("GCOUN_NM") String gcounNm,
            @RequestParam("GCOUN_DT") LocalDateTime gcounDt,
            @RequestParam("GCOUN_DTL_CN") String place,
            @RequestParam("GCOUN_BGNG_DT") LocalDate gcounBgngDt,
            @RequestParam("GCOUN_DDLN_DT") LocalDate gcounDdlnDt,
            @RequestParam("GCOUN_LMT_COUNT") int gcounLmtCount,
            @RequestPart("file") MultipartFile file) throws Exception {
		
		// DTO 객체 생성 및 설정
        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setCSL_NO(cslNo);
        adminDTO.setGCOUN_NM(gcounNm);
        adminDTO.setGCOUN_DT(gcounDt);
        adminDTO.setGCOUN_DTL_CN(place);
        adminDTO.setGCOUN_BGNG_DT(gcounBgngDt);
        adminDTO.setGCOUN_DDLN_DT(gcounDdlnDt);
        adminDTO.setGCOUN_LMT_COUNT(gcounLmtCount);
		
		// 저장 경로 지정
        String path = System.getProperty("user.dir") + "/src/main/resources/static/gcounFiles";
        
		File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        // 랜덤 이름 생성
        UUID uuid = UUID.randomUUID();
        
        // 파일 이름 생성
        String fileName = uuid + "_" + file.getOriginalFilename();
        
        File saveFile = new File(path, fileName);
        file.transferTo(saveFile);
        
        adminDTO.setGCOUN_FILENM(fileName);
        adminDTO.setGCOUN_CONTS_CN("/gcounFiles/" + fileName);
        
        int gcounEnroll = adminService.gcounEnroll(adminDTO);
		
        return gcounEnroll;
	}
	
	// 심리상담
	@GetMapping("/admin/pcoun")
	public String pcoun() {
		
		return "admin/pcoun";
	}

	@PostMapping("/admin/registerEmpCounselor")
    @ResponseBody
    public String registerCounselor(@RequestParam("cd") String cd, @RequestParam("CSL_NO") String cslNo,
    		@RequestParam("CSL_NM") String cslNm, @RequestParam("CSL_EMAIL") String email, @RequestParam("CSL_MOBILE") String mobile, 
    		@RequestParam("SCSBJT_NM") String scsbjtNm,
    		 @RequestPart("file") MultipartFile file) throws Exception {
		
		System.out.println(cd);
		System.out.println(cslNo);
		System.out.println(cslNm);
		System.out.println(email);
		System.out.println(mobile);
		System.out.println(scsbjtNm);
		System.out.println(file);
		
		// DTO 객체 생성 및 설정
        EcounAdDTO ecounAdDTO = new EcounAdDTO();
        ecounAdDTO.setCslNo(cslNo);
        ecounAdDTO.setCslNm(cslNm);
        ecounAdDTO.setEmail(email);
        ecounAdDTO.setMobile(mobile);
        ecounAdDTO.setScsbjtNm(scsbjtNm);
        
        // 저장 경로 지정
        String path = System.getProperty("user.dir") + "/src/main/resources/static/ecounFiles";
        //String path = "/src/main/resources/static/ecounFiles";
        
		File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        // 랜덤 이름 생성
        UUID uuid = UUID.randomUUID();
        
        // 파일 이름 생성
        String fileName = uuid + "_" + file.getOriginalFilename();
        
        File saveFile = new File(path, fileName);
        file.transferTo(saveFile);
        
        ecounAdDTO.setECOUN_FILENM(fileName);
        ecounAdDTO.setECOUN_CONTS_CN("/ecounFiles/" + fileName);
        
        System.out.println(fileName);
        
        adminService.ecounEnroll(ecounAdDTO);
        adminService.registerEmpCounPro(ecounAdDTO);
        
        //System.out.println(cd);
        //System.out.println(cslNm);
        //System.out.println(formData);
        //System.out.println(file);
        //adminService.registerCounselor(formData);
        //adminService.registerEmpCounPro(formData);

        // 데이터베이스에 상담사 등록 로직 추가
        	
        return "success";
    }
	
}
