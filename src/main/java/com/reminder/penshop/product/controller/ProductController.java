package com.reminder.penshop.product.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.reminder.penshop.member.model.service.MemberService;
import com.reminder.penshop.paging.model.dto.Criteria;
import com.reminder.penshop.paging.model.dto.ItemCriteria;
import com.reminder.penshop.paging.model.dto.PageDTO;
import com.reminder.penshop.product.model.dto.BrandDTO;
import com.reminder.penshop.product.model.dto.CategoryDTO;
import com.reminder.penshop.product.model.dto.OptionDTO;
import com.reminder.penshop.product.model.dto.ProductDTO;
import com.reminder.penshop.product.model.dto.StockDTO;
import com.reminder.penshop.product.model.service.ProductService;
import com.reminder.penshop.review.model.dto.ReviewDTO;
import com.reminder.penshop.review.model.service.ReviewService;
import com.reminder.penshop.upload.model.dto.AttachmentDTO;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

@Slf4j
@Controller
@SessionAttributes({"loginMember", "penshopCart", "recentlyViewed"})
public class ProductController {
	
	//썸네일 크기
	public static final int THUMB_WIDTH_SIZE = 540;
	public static final int THUMB_HEIGHT_SIZE = 540;
	
	private final ProductService productService;
	private final ReviewService reviewService;
	private final MemberService memberService;
	private final MessageSource messageSource;
	
	@Autowired
	public ProductController(ProductService productService, ReviewService reviewService, MemberService memberService, MessageSource messageSource) {
		this.productService = productService;
		this.reviewService = reviewService;
		this.memberService = memberService;
		this.messageSource = messageSource;
	}
	
	/**
	 * 상품등록 페이지 이동
	 */
	@GetMapping("/admin/product/add")
	public void addProduct(Model model) {
		/* 카테고리 목록 호출 */
		List<CategoryDTO> category = productService.getCategoryList();
		model.addAttribute("category", category);
		
		/* 브랜드 목록 호출 */
		List<BrandDTO> brand = productService.getBrandList();
		model.addAttribute("brand", brand);
	}
	
	/**
	 * 카테고리 중복 검사 및 새 카테고리 추가
	 */
	@PostMapping(value="/admin/product/checkCategory", produces="application/json; charset=UTF-8")
	@ResponseBody
	public ModelAndView checkCategoryName(@RequestBody Map<String, String> param, Locale locale) {
		/* jsonView 적용 */
		ModelAndView mv = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		mv.setView(jsonView);
		
		/* 카테고리 중복 검사 */
		String categoryName = param.get("category").replaceAll("\\s", ""); //문자 사이 공백제거 후 비교
		log.info(categoryName);
		int categoryCount = productService.checkCategoryName(categoryName);
		log.info("카테고리 중복 조회 결과 : {}", categoryCount);
		
		if(categoryCount > 0) { //이미 존재하는 카테고리
			String errorMessage = messageSource.getMessage("errorWhileAddingANewCategory", null, locale);
			mv.addObject("errorMessage", errorMessage);
			log.info(errorMessage);
		}
		
		if(categoryCount == 0) { //새 카테고리 추가
			String categorySection = param.get("section");
			int result = productService.addANewCategory(categoryName, categorySection);
			if(result == 1) {
				log.info("새 카테고리 추가 완료 : {}", categoryName);
			}
		}
		return mv;
	}
	
	/**
	 * 브랜드 중복 검사 및 새 브랜드 추가
	 */
	@PostMapping(value="/admin/product/checkBrand", produces="application/json; charset=UTF-8")
	@ResponseBody
	public ModelAndView checkBrandName(@RequestBody Map<String, String> param, Locale locale) {
		/* jsonView 적용 */
		ModelAndView mv = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		mv.setView(jsonView);
		
		/* 브랜드 중복 검사 */
		String brandName = param.get("brand").replaceAll("\\s", ""); //문자 사이 공백제거 후 비교
		log.info(brandName);
		int brandCount = productService.checkBrandName(brandName);
		log.info("브랜드 중복 조회 결과 : {}", brandCount);
		
		if(brandCount > 0) { //이미 존재하는 브랜드
			String errorMessage = messageSource.getMessage("errorWhileAddingANewBrand", null, locale);
			mv.addObject("errorMessage", errorMessage);
			log.info(errorMessage);
		}
		
		if(brandCount == 0) { //새 브랜드 추가
			int result = productService.addANewBrand(brandName);
			if(result == 1) {
				log.info("새 브랜드 추가 완료 : {}", brandName);
			}
		}
		return mv;
	}
	
	/**
	 * 새 상품 및 상품 썸네일 등록
	 * !Spring Boot 개발 환경에서는 src/main 하위에 webapp 폴더를 직접 생성해야 함!
	 */
	@PostMapping(value="/admin/product/add", consumes={MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	@ResponseBody
	public ModelAndView addProduct(@RequestPart("params") Map<String, Object> params, @RequestParam(value="files", required=false) List<MultipartFile> files, HttpServletRequest request, HttpServletResponse response, Locale locale) {
		/* jsonView 적용 */
		ModelAndView mv = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		mv.setView(jsonView);
		
		/* 상품 추가 */
		String categoryName = params.get("category").toString();
		int categoryNo = productService.checkCategoryNo(categoryName);
		String prodName = params.get("prodName").toString();
		String prodDesc = params.get("prodDesc").toString();
		String productTag = params.get("tagArr").toString().replace("$", ",");
		int discountRate = Integer.parseInt(params.get("discountRate").toString());
		int prodPrice = Integer.parseInt(params.get("prodPrice").toString());
		String brandName = params.get("brand").toString();
		int brandNo = productService.checkBrandNo(brandName);
		log.info("brandName : {}", brandName);
		log.info("brandNo : {}", brandNo);
		String prodOrigin = params.get("prodOrigin").toString();
		String prodDetailContent = params.get("prodDetailContent").toString();
		
		//ProductDTO 객체에 값으로 설정
		ProductDTO product = new ProductDTO();
		product.setCategoryNo(categoryNo);
		product.setProdName(prodName);
		product.setProdDesc(prodDesc);
		product.setProductTag(productTag);
		product.setDiscountRate(discountRate);
		product.setProdPrice(prodPrice);
		product.setBrandNo(brandNo);
		product.setProdOrigin(prodOrigin);
		product.setProdDetailContent(prodDetailContent);
		
		//상품 정보 추가
		int addresult = productService.addProduct(categoryNo, prodName, prodDesc, productTag, discountRate, prodPrice, brandNo, prodOrigin, prodDetailContent);
		
		int totalOptionNumber = Integer.parseInt(params.get("optArrLength").toString());
		log.info("사용자가 추가한 총 옵션 개수 : {}", totalOptionNumber);
		
		String[] bodyColors = params.get("bodyColor").toString().replace("[", "").replace("]", "").replaceAll("[\\t\\s]","").split(",");
		String[] inkColors = params.get("inkColor").toString().replace("[", "").replace("]", "").replaceAll("[\\t\\s]","").split(",");
		String[] pointSizes = params.get("pointSize").toString().replace("[", "").replace("]", "").replaceAll("[\\t\\s]","").split(",");
		String[] stockAmounts = params.get("stockAmount").toString().replace("[", "").replace("]", "").replaceAll("[\\t\\s]","").split(",");
		String[] extraCharges = params.get("extraCharge").toString().replace("[", "").replace("]", "").replaceAll("[\\t\\s]","").split(",");
		
		StockDTO stock = new StockDTO();
		OptionDTO option = new OptionDTO();
		
		for(int i=0; i < totalOptionNumber; i++) {
			
			//입고 추가
			int amount = Integer.parseInt(stockAmounts[i].toString());
			productService.addProductStock(amount);
			
			//옵션 추가
			String bodyColor = bodyColors[i].toString();
			String inkColor = inkColors[i].toString();
			double pointSize = Double.parseDouble(pointSizes[i].toString());
			int extraCharge = Integer.parseInt(extraCharges[i].toString());
			
			option.setBodyColor(bodyColor);
			option.setInkColor(inkColor);
			option.setPointSize(pointSize);
			option.setExtraCharge(extraCharge);
			
			productService.addProductOption(option.getBodyColor(), option.getInkColor(), option.getPointSize(), option.getExtraCharge());
		}
		
		log.info("상품 썸네일 추가 시작");
		
		String realPath = request.getSession().getServletContext().getRealPath("/");
		log.info("src/main/webapp : {}", realPath);
		
		String originalUploadPath = realPath + "upload" + File.separator + "product" + File.separator + "original";
		String thumbnailUploadPath = realPath + "upload" + File.separator + "product" + File.separator + "thumbnail";
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
				
				String origFileUrl = "/upload/product/original/" + uuid.toString().replace("-", "") + "." + extension;
				fileMap.put("origFileName", origFileName);
				fileMap.put("saveFileName", randomFileName);
				fileMap.put("savePath", origFileUrl);
				
				//썸네일 파일을 thumbnail 폴더에 저장
				Thumbnails.of(originalUploadPath + File.separator + randomFileName) //썸네일로 변환 후 저장
						  .size(THUMB_WIDTH_SIZE, THUMB_HEIGHT_SIZE)
						  .toFile(thumbnailUploadPath + File.separator + "thumbnail_" + randomFileName);
				fileMap.put("thumbnailPath", "/upload/product/thumbnail/thumbnail_" + randomFileName); //웹서버에서 접근 가능한 형태로 썸네일의 저장 경로 작성
				
				fileList.add(fileMap);
				
				//현재 상품번호 조회
				int currProdNo = productService.checkCurrProdNo();
				
				//product 객체의 AttachmentList 설정
				product.setAttachmentList(new ArrayList<AttachmentDTO>());
				List<AttachmentDTO> list = product.getAttachmentList();
				log.info("fileList size : {}", fileList.size());
				
				AttachmentDTO tempFileInfo = new AttachmentDTO();
				for(int i=0; i < fileList.size(); i++) {
					tempFileInfo.setRefProdNo(currProdNo);
					tempFileInfo.setOrigFileName(fileList.get(i).get("origFileName"));
					tempFileInfo.setSaveFileName(fileList.get(i).get("saveFileName"));
					tempFileInfo.setSavePath(fileList.get(i).get("savePath"));
					tempFileInfo.setThumbnailPath(fileList.get(i).get("thumbnailPath"));
					
					if(i == 0) { //index 기준으로 첫번째 첨부 이미지는 메인썸네일, 그 다음은 서브썸네일에 해당
						tempFileInfo.setFileType("THUMB_MAIN");
					} else {
						tempFileInfo.setFileType("THUMB_SUB");
					}
					
					
					list.add(tempFileInfo);
				}
				productService.attachProdThumbnail(tempFileInfo);
			} catch (IOException e) { e.printStackTrace(); }
		}
		
		if(addresult == 1) {
			String successMessage = messageSource.getMessage("productAddedSuccessfully", null, locale);
			mv.addObject("successMessage", successMessage);
		} else {
			String errorMessage = messageSource.getMessage("errorWhileAddingAProduct", null, locale);
			mv.addObject("errorMessage", errorMessage);
		}
		
		return mv;
	}
	
	/**
	 * 상품 상세내용이미지(CKEditor4 첨부 이미지) 업로드
	 * !Spring Boot 개발 환경에서는 src/main 하위에 webapp 폴더를 직접 생성해야 함!
	 */
	@PostMapping("/admin/product/add/contentImageUpload")
	public void uploadProdContentImage(HttpServletRequest request, HttpServletResponse response, @RequestParam MultipartFile upload) {
		
		OutputStream out = null; //지정된 공간에 이미지 파일들을 내보내기하여 저장
		PrintWriter printWriter = null; //View에 띄워줄 구문 작성
		
		response.setCharacterEncoding("UTF-8"); //인코딩
		response.setContentType("text/html; charset=UTF-8");
		
		AttachmentDTO attachment = new AttachmentDTO();
		try {
			String realPath = request.getSession().getServletContext().getRealPath("/");
			log.info("src/main/webapp : {}", realPath);
			String imageUploadPath = realPath + "upload" + File.separator + "product" + File.separator + "content";
			File contentImageDirectory = new File(imageUploadPath);
			if(!contentImageDirectory.exists()) { //지정 폴더가 존재하지 않을 시 생성
				contentImageDirectory.mkdirs(); //thumbnail보다 content가 먼저 등록되므로 product/content 폴더를 연달아 생성하기 위해 mkdir()이 아닌 mkdirs()로 작성
			}
			
			UUID uuid = UUID.randomUUID(); //랜덤 문자 생성
			
			byte[] bytes = upload.getBytes();
			
			String origFileName = upload.getOriginalFilename(); //원본파일명
			String extension = FilenameUtils.getExtension(origFileName);
			//int dot = origFileName.lastIndexOf(".");
			//String ext = origFileName.substring(dot);
			String saveFileName = imageUploadPath + File.separator + uuid.toString().replace("-", "") + "." + extension; //저장파일명
			
			//이미지 저장
			out = new FileOutputStream(saveFileName);
			out.write(bytes);
			out.flush();
			
			//CKEditor로 전송
			printWriter = response.getWriter();
			String callback = request.getParameter("CKEditorFuncNum");
			String fileUrl = "/upload/product/content/" + uuid.toString().replace("-", "") + "." + extension; //main 하위의 저장 경로 이름 따라서 file url 설정
			
			printWriter.println("<script type='text/javascript'>"
					+ "window.parent.CKEDITOR.tools.callFunction("
					+ callback + ",'" + fileUrl + "',' 이미지를 업로드하였습니다.')"
					+"</script>");
			printWriter.flush();
			
			//DB 전송
			attachment.setOrigFileName(origFileName);
			int getLastSeparator = saveFileName.lastIndexOf("\\");
			attachment.setSaveFileName(saveFileName.substring(getLastSeparator));
			attachment.setSavePath(fileUrl);
			attachment.setThumbnailPath("NULL");
			attachment.setFileType("CONTENT");
			
			productService.attachProdContentImage(attachment);
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(out != null) { out.close(); }
				if(printWriter != null) { printWriter.close(); }
			} catch(IOException e) { e.printStackTrace(); }
		}
	}
	
	/**
	 * 상품 목록 조회
	 */
	@GetMapping("/admin/product/list")
	public void getProductList(@Valid @ModelAttribute("criteria") Criteria criteria, BindingResult bindingResult, HttpServletRequest request, Model model) {
		log.info("상품 목록 요청");
		
		/* 상품수 */
		int total = productService.getTotalNumber(criteria); //전체
		int onSale = productService.getOnSaleNumber(criteria); //판매중
		int stopSale = productService.getStopSaleNumber(criteria); //판매중지
		int onDiscount = productService.getOnDiscountNumber(criteria); //할인중
		int soldOut = productService.getSoldOutNumber(criteria); //품절
		
		/* 상품 목록 조회 */
		List<ProductDTO> productList = productService.getProductList(criteria); //전체
		List<ProductDTO> onSaleOnly = productService.getOnSaleOnly(criteria); //판매중
		List<ProductDTO> stopSaleOnly = productService.getStopSaleOnly(criteria); //판매중지
		List<ProductDTO> onDiscountOnly = productService.getOnDiscountOnly(criteria); //할인중
		List<ProductDTO> soldOutOnly = productService.getSoldOutOnly(criteria); //품절
		
		List<Integer> allProdNo = productService.getAllProdNo();
		List<AttachmentDTO> thumbnailList = new ArrayList<>();
		List<OptionDTO> optionList = new ArrayList<>();
		List<Map<Integer, Integer>> salesList = new ArrayList<>();
		for(int prodNo: allProdNo) {
			/* 상품별 메인썸네일 조회 */
			AttachmentDTO thumbnail = productService.getMainThumbnailByProdNo(prodNo);
			thumbnailList.add(thumbnail);
			/* 상품별 옵션 조회 */
			List<OptionDTO> options = productService.getOptionListByProdNo(prodNo);
			for(OptionDTO option : options) {
				optionList.add(option);
			}
			/* 상품별 누적 판매량 조회 */
			Map<Integer, Integer> salesMap = new HashMap<>();
			Integer sales = productService.getSalesByProdNo(prodNo); //null 값을 취급할 수 있도록 Integer 타입으로 설정
			if(sales == null) sales = 0;
			salesMap.put(prodNo, sales);
			salesList.add(salesMap);
		}
		log.info("상품 목록 조회 완료");
		
		model.addAttribute("total", total);
		model.addAttribute("onSale", onSale);
		model.addAttribute("stopSale", stopSale);
		model.addAttribute("onDiscount", onDiscount);
		model.addAttribute("soldOut", soldOut);
		
		model.addAttribute("productList", productList);
		model.addAttribute("onSaleOnly", onSaleOnly);
		model.addAttribute("stopSaleOnly", stopSaleOnly);
		model.addAttribute("onDiscountOnly", onDiscountOnly);
		model.addAttribute("soldOutOnly", soldOutOnly);
		
		model.addAttribute("thumbnailList", thumbnailList);
		model.addAttribute("optionList", optionList);
		model.addAttribute("salesList", salesList);
		
		model.addAttribute("pageMaker", new PageDTO(productService.getTotalNumber(criteria), 10, criteria));
		model.addAttribute("pageMakerWithOnSaleNumber", new PageDTO(productService.getOnSaleNumber(criteria), 10, criteria));
		model.addAttribute("pageMakerWithStopSaleNumber", new PageDTO(productService.getStopSaleNumber(criteria), 10, criteria));
		model.addAttribute("pageMakerWithOnDiscountNumber", new PageDTO(productService.getOnDiscountNumber(criteria), 10, criteria));
		model.addAttribute("pageMakerWithSoldOutNumber", new PageDTO(productService.getSoldOutNumber(criteria), 10, criteria));
	}
	
	/**
	 * 상품 상세 정보 조회 및 수정
	 */
	@GetMapping("/admin/product/edit")
	public void editProductDetails(@RequestParam("no") int prodNo, Model model) {
		/* 카테고리 목록 호출 */
		List<CategoryDTO> category = productService.getCategoryList();
		/* 브랜드 목록 호출 */
		List<BrandDTO> brand = productService.getBrandList();
		
		/* 상품 상세 정보 호출 */
		ProductDTO detail = productService.getProductDetails(prodNo);
		/* 상세 정보 중 태그값 가공 */
		String[] tagArr = detail.getProductTag().split(",");
		Map<Integer, String> tagMap = new HashMap<>(); //타임리프 반복문이 중첩 사용될 예정이기에 임시 index 용도로 활용할 Integer 값을 key로 담아 Map 타입 선언
		for(int i=0; i < tagArr.length; i++) {
			tagMap.put(i, tagArr[i]);
			//log.info(tagMap.get(i));
		}
		/* 상세 정보 중 옵션값 가공 */
		List<OptionDTO> option = productService.getOptionListByProdNo(prodNo);
		
		/* 상품 썸네일 조회 */
		AttachmentDTO mainThumb = productService.getMainThumbnailByProdNo(prodNo);
		AttachmentDTO subThumb = productService.getSubThumbnailByProdNo(prodNo);
		
		model.addAttribute("category", category);
		model.addAttribute("brand", brand);
		model.addAttribute("detail", detail);
		model.addAttribute("tagMap", tagMap);
		model.addAttribute("option", option);
		model.addAttribute("mainThumb", mainThumb);
		model.addAttribute("subThumb", subThumb);
	}
	
	/**
	 * 상품 판매여부 변경
	 */
	@PostMapping(value="/admin/product/manageSale", produces="application/json; charset=UTF-8")
	@ResponseBody //생략 시 이동할 VIEW를 찾게 되므로 String 값 자체를 보내기 위해서는 반드시 붙여써야 함
	public String changeAvailability(@RequestParam Map<String, Object> params, HttpServletRequest request) {
		log.info("상품 판매여부 수정 시작");
		String optValue = params.get("optValue").toString(); //선택값
		String selected = "";
		if(Integer.parseInt(optValue) == 2) { //판매중지
			selected = "N";
		} else if(Integer.parseInt(optValue) == 1) { //판매중
			selected = "Y";
		}
		String[] prodList = request.getParameterValues("arr"); //상품번호
		int count = 0;
		String result = "";
		
		log.info("선택된 총 상품 개수 : {}", prodList.length);
		for(int i=0; i < prodList.length; i++) {
			String current = productService.searchSaleYnByProdNo(Integer.parseInt(prodList[i]));
			log.info("현재 판매여부 : {}", current);
			log.info("선택한 판매여부 : {}", selected);
			
			if(selected.equals("N")) { //현재 판매중에서 판매중지로 변경
				count += productService.stopSellingAProduct(Integer.parseInt(prodList[i]));
			} else { //현재 판매중지에서 판매중으로 변경
				count += productService.putAProductOnSale(Integer.parseInt(prodList[i]));
			}
			
			if(prodList.length == count) {
				result = "성공";
			}
			log.info(result);
		}
		return result;
	}
	
	/**
	 * 상품 목록에서 완전삭제
	 */
	@PostMapping(value="/admin/product/deleteProduct", produces="application/json; charset=UTF-8")
	@ResponseBody
	public String deleteProduct(@RequestParam("no") int prodNo, Model model) {
		String result = "";
		int count = productService.deleteProduct(prodNo);
		if(count == 1) { result = "성공"; }
		return result;
	}
	
	/**
	 * 상품 상세 페이지
	 */
	@GetMapping("/product/details")
	public void getProductDetails(@RequestParam("no") int prodNo, HttpSession session, Model model) {
		/* 최근 본 상품으로 저장 */
		List<Integer> recentlyViewed = (List<Integer>) session.getAttribute("recentlyViewed");
		if(recentlyViewed == null) recentlyViewed = new ArrayList<>();
		if(recentlyViewed.size() == 0) {
			recentlyViewed.add(prodNo);
		} else {
			if(recentlyViewed.indexOf(prodNo)== -1) recentlyViewed.add(prodNo); //중복 방지
		}
		log.info("recently viewed items: {}", recentlyViewed);
		session.removeAttribute("recentlyViewed");
		session.setAttribute("recentlyViewed", recentlyViewed);
		
		session.removeAttribute("orderItem"); //주문 요청 시마다 주문목록 session 갱신
		
		/* 상품 상세 정보 호출 */
		ProductDTO detail = productService.getProductDetailsWithIncrementViewCount(prodNo); //getProductDetails()는 전역적으로 사용되고 있어 상세페이지 조회용을 별도로 구분
		
		/* 상품 썸네일 조회 */
		AttachmentDTO mainThumb = productService.getMainThumbnailByProdNo(prodNo);
		AttachmentDTO subThumb = productService.getSubThumbnailByProdNo(prodNo);
		
		/* 상세 정보 중 옵션값 가공 */
		List<OptionDTO> option = productService.getOptionListByProdNo(prodNo);
		List<String> bodyColor = new ArrayList<>();
		for(OptionDTO o : option) { //바디컬러 단일-세트상품별 값 가공 및 중복값 제거
			if(o.getBodyColor().equals("(해당없음)")) { //조회된 바디컬러가 0개인 경우
				bodyColor.add("(해당없음)");
			}
			if(o.getBodyColor().contains("$")) { //조회된 바디컬러가 2개 이상인 경우(세트상품)
				String[] colors = o.getBodyColor().split("\\$");
				for(int i=0; i < colors.length; i++) {
					if(!bodyColor.contains(colors[i])) {
						//log.info(colors[i]);
						bodyColor.add(colors[i]);
					}
				}
			} else {
				if(bodyColor.contains(o.getBodyColor())) {
					continue;
				}
				bodyColor.add(o.getBodyColor());
			} 
		}
		List<String> inkColor = new ArrayList<>();
		for(OptionDTO o : option) { //잉크컬러 단일-세트상품별 값 가공 및 중복값 제거
			//log.info(o.getInkColor());
			if(o.getInkColor().equals("(해당없음)")) { //조회된 잉크컬러가 0개인 경우
				inkColor.add("(해당없음)");
			} else if(o.getInkColor().contains("$")) { //조회된 잉크컬러가 2개 이상인 경우(세트상품)
				String[] colors = o.getInkColor().split("\\$");
				for(int i=0; i < colors.length; i++) {
					if(!inkColor.contains(colors[i])) {
						//log.info(colors[i]);
						inkColor.add(colors[i]);
					}
				}
			} else {
				if(inkColor.contains(o.getInkColor())) {
					continue;
				}
				inkColor.add(o.getInkColor());
			} 
		}
		List<String> pointSize = new ArrayList<>();
		for(OptionDTO o: option) { //심두께 중복값 제거
			if(pointSize.contains(String.valueOf(o.getPointSize()))) {
				continue;
			}
			pointSize.add(String.valueOf(o.getPointSize()));
		}
		
		List<String> tagList = new ArrayList<>();
		for(OptionDTO o : option) { //태그 중복값 제거
			String[] tags = detail.getProductTag().split("\\[|\\]|,| "); //여러 개의 구분자를 사용하기 위해서는 버티컬바(|)로 구분 필요, 대괄호는 이스케이프 문자 적용 대상
			for(int i=0; i < tags.length; i++) {
				if(!tagList.contains(tags[i])) {
					tagList.add(tags[i]);
				}
				//log.info(tags[i]);
			}
		}
		
		/* 상품별 리뷰 목록 조회 */
		List<ReviewDTO> reviewList = productService.getReviewListByProdNo(prodNo); //첨부파일 포함 리뷰
		Map<String, Integer> attachmentMap = new HashMap<>(); //리뷰별 첨부파일
		for(int i=0; i < reviewList.size(); i++) {
			int reviewNo = reviewList.get(i).getReviewNo();
			for(int j=1; j <= 3; j++) {
				String thumbnailPath = reviewService.getAttachmentByReviewNo(reviewNo, j);
				if(thumbnailPath != null) {
					log.info("attachment : {}", thumbnailPath);
					attachmentMap.put(thumbnailPath, reviewNo);
				}
			}
		}
		int total = productService.getTotalNumberOfReviews(prodNo); //첨부파일 제외 개수
		if(reviewList.size() > 0) {
			double averageRating = productService.averageReviewRating(prodNo);
			List<Integer> percentageList = new ArrayList<>();
			List<Integer> numberList = new ArrayList<>();
			for(int i=1; i <= 5; i++) { //1~5 평점 비율
				int percentage = productService.getPercentageOfRating(total, prodNo, i);
				int number = productService.getNumberOfRatings(prodNo, i);
				percentageList.add(percentage);
				numberList.add(number);
				log.info("i : {}, {}", percentage, number);
			}
			model.addAttribute("averageRating", averageRating);
			model.addAttribute("percentageList", percentageList);
			model.addAttribute("numberList", numberList);
		}
		
		/* 회원의 경우 위시리스트 상품은 별도 표시 */
		String loginMember = (String) session.getAttribute("loginMember");
		if(loginMember != null) {
			log.info("loginMember : {}", loginMember);
			List<Integer> memberWishItem = memberService.getProdNoFromWishList(loginMember);
			model.addAttribute("memberWishItem", memberWishItem);
		}
		
		model.addAttribute("detail", detail);
		model.addAttribute("option", option);
		model.addAttribute("bodyColor", bodyColor);
		model.addAttribute("inkColor", inkColor);
		model.addAttribute("pointSize", pointSize);
		model.addAttribute("tagList", tagList);
		model.addAttribute("mainThumb", mainThumb);
		model.addAttribute("subThumb", subThumb);
		model.addAttribute("reviewList", reviewList);
		model.addAttribute("attachmentMap", attachmentMap);
		model.addAttribute("total", total);
	}
	
	/**
	 * 상품 목록
	 */
	@GetMapping("/product/list")
	public void getProductListByCategory(@Valid @ModelAttribute("itemCriteria") ItemCriteria itemCriteria, HttpSession session, Model model) {
		String section = itemCriteria.getSection();
		log.info("요첨 section : {}", section);
		log.info("요첨 itemCriteria : {}", itemCriteria);
		itemCriteria.setSection(section); //대분류 카테고리 섹션
		List<ProductDTO> sortedList = productService.getProductListByCategorySection(itemCriteria);
		List<ProductDTO> productList = new ArrayList<>();
		for(int i=0; i < sortedList.size(); i++) {
			int sortedProdNo = sortedList.get(i).getProdNo();
			//log.info("sortedProdNo : {}", sortedProdNo);
			ProductDTO productDTO = productService.getProductDetails(sortedProdNo);
			productList.add(productDTO);
		}
		List<AttachmentDTO> thumbnailList = new ArrayList<>();
		Map<Integer, Integer> reviewNumberMap = new HashMap<>();
		Map<Integer, Double> reviewRatingMap = new HashMap<>();
		for(int i=0; i < productList.size(); i++) { //출력용 상품별 메인썸네일, 리뷰수, 평점
			int prodNo = productList.get(i).getProdNo();
			AttachmentDTO mainThumb = productService.getMainThumbnailByProdNo(prodNo);
			thumbnailList.add(mainThumb);
			int number = productService.getTotalNumberOfReviews(prodNo);
			reviewNumberMap.put(prodNo, number);
			if(number > 0) {
				double averageRating = productService.averageReviewRating(prodNo);
				reviewRatingMap.put(prodNo, averageRating);
			} else {
				reviewRatingMap.put(prodNo, 0.0);
			}
		}
		log.info("productList : {}", productList);
		
		/* 회원의 경우 위시리스트 상품은 별도 표시 */
		String loginMember = (String) session.getAttribute("loginMember");
		if(loginMember != null) {
			log.info("loginMember : {}", loginMember);
			List<Integer> memberWishItem = memberService.getProdNoFromWishList(loginMember);
			model.addAttribute("memberWishItem", memberWishItem);
		}
		
		/* 섹션별 구성 정보 */
		if(section != null) {
			List<String> categoryBySection = productService.getCategoryListBySection(section);
			Map<String, Integer> totalNumberMap = new HashMap<>();
			for(int i=0; i < categoryBySection.size(); i++) {
				int num = productService.getTotalNumberByMinorCategory(categoryBySection.get(i));
				totalNumberMap.put(categoryBySection.get(i), num);
			}
			List<String> brandBySection = productService.getBrandNameBySection(section);
			
			model.addAttribute("categoryBySection", categoryBySection);
			model.addAttribute("brandBySection", brandBySection);
			model.addAttribute("totalNumberMap", totalNumberMap);
		}
		
		/* 필터 결과에 따라 페이징 처리 */
		int totalNumber = productService.getTotalNumberByCriteria(itemCriteria);
		
		model.addAttribute("section", section == null || section == "" ? "전체 상품" : section);
		model.addAttribute("productList", productList);
		model.addAttribute("thumbnailList", thumbnailList);
		model.addAttribute("reviewNumberMap", reviewNumberMap);
		model.addAttribute("reviewRatingMap", reviewRatingMap);
		model.addAttribute("total", totalNumber);
		model.addAttribute("pageMaker", new PageDTO(totalNumber, 10, itemCriteria));
	}
	
	@GetMapping("/product/search")
	public void searchProduct(@RequestParam("keyword") String keyword, Model model) {
		List<ProductDTO> resultList = productService.searchProductByKeyword(keyword);
		List<AttachmentDTO> thumbnailList = new ArrayList<>();
		Map<Integer, Integer> reviewNumberMap = new HashMap<>();
		Map<Integer, Double> reviewRatingMap = new HashMap<>();
		for(int i=0; i < resultList.size(); i++) { //출력용 상품별 메인썸네일, 리뷰수, 평점
			int prodNo = resultList.get(i).getProdNo();
			AttachmentDTO mainThumb = productService.getMainThumbnailByProdNo(prodNo);
			thumbnailList.add(mainThumb);
			int number = productService.getTotalNumberOfReviews(prodNo);
			reviewNumberMap.put(prodNo, number);
			if(number > 0) {
				double averageRating = productService.averageReviewRating(prodNo);
				reviewRatingMap.put(prodNo, averageRating);
			} else {
				reviewRatingMap.put(prodNo, 0.0);
			}
		}
		model.addAttribute("keyword", keyword);
		model.addAttribute("total", resultList.size());
		model.addAttribute("resultList", resultList);
		model.addAttribute("thumbnailList", thumbnailList);
		model.addAttribute("reviewNumberMap", reviewNumberMap);
		model.addAttribute("reviewRatingMap", reviewRatingMap);
	}
}
