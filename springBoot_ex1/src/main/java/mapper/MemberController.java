package mapper;

import java.io.File;
import java.io.IOException;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import model.Board;
import model.Member;
import service.BoardDaoMybatis;
import service.MemberDaoMybatis;

@Controller
@RequestMapping("/member/")
public class MemberController {

	@Autowired
	MemberDaoMybatis dao;

	@RequestMapping("test")
	public String pub(Model m) {
		m.addAttribute("test", "member �Դϴ�");
		return "index";

	}

	@RequestMapping("main")
	public String main(HttpSession session, Model m) {
		/*
		 * 1. �α��� �Ŀ� �������� ������. => �α��� ���� Ȯ�� => �α��λ��°� �ƴ� ���, loginForm.jsp�� ������ �̵��ϱ�
		 */
		String login = (String) session.getAttribute("login");

		if (login == null || login.trim().equals("")) {
			return "member/loginForm";
		} else {

			return "member/main";
		}
	}

	@RequestMapping("join")
	public String join(Member mem, Model m) {
		int num = dao.insert(mem);
		String msg = "";
		String url = "";
		if (num > 0) { // ���Լ���
			msg = mem.getName() + "���� ������ �Ϸ� �Ǿ����ϴ�";
			url = "loginForm";
		} else {
			msg = "ȸ�� ������ ���� �Ǿ����ϴ�";
			url = "joinForm";
		}
		m.addAttribute("msg", msg);
		m.addAttribute("url", url);

		return "alert";

	}

	@RequestMapping("login")
	public String login(HttpSession session, String id, String pass, Model m) {

		/*
		 * 1. id, pass �Ķ���� ���� 2. db���� id �� �ش��ϴ� �����͸� �о Member ���޹ޱ� 3. ����м� Member��ü��
		 * null �ΰ�� : ���̵� Ȯ���ϼ��� �޼��� ���. --->loginForm.jsp ������ �̵� Member��ü�� null�� �ƴ� ���
		 * : ȭ�鿡�� �Էµ� ��й�ȣ�� db ��й�ȣ ���� ������� : �α��� ����. -----> main.jsp ������ �̵� �ٸ� ��� :
		 * ��й�ȣ Ȯ���ϼ��� ----> loginForm.jsp�� ������ �̵�
		 */

		// mem : db�� ����� ȸ������ ����
		Member mem = dao.selectOne(id);
		String msg = "���̵� Ȯ���ϼ���";
		String url = "loginForm";
		if (mem != null) {
			if (pass.equals(mem.getPass())) {
				session.setAttribute("login", id);
				msg = mem.getName() + "���� �α��� �ϼ̽��ϴ�.";
				url = "main";
			} else {
				msg = "��й�ȣ�� Ȯ�� �ϼ���";
			}
		}
		m.addAttribute("url", url);
		m.addAttribute("msg", msg);
		return "alert";
	}

	@RequestMapping("logout")
	public String logout(HttpSession session, Model m) throws Throwable {

		/*
		 * 1. session�� ��ϵ� �α��� ���� ���� 2. id�� �α׾ƿ��Ǿ����ϴ�. �޼��� ��� �� loginForm.jsp�� ������ �̵��ϱ�
		 */

		String login = (String) session.getAttribute("login");
		session.invalidate();
		String msg = login + "�� �α׾ƿ��Ǿ����ϴ�.";
		m.addAttribute("msg", msg);
		m.addAttribute("url", "loginForm");
		return "alert";
	}

	@RequestMapping("memberInfo")
	public String memberInfo(HttpSession session, String id, Model m) throws Throwable {
		/*
		 * 1. id �Ķ���Ͱ� �����ϱ� 2. �α׾ƿ����� : �α����� �ʿ��մϴ�. �޼��� ����ϰ�, loginForm.jsp ������ �̵� 3.
		 * �α��� ���� 3-1 : id �Ķ���������� login ������ ���ؼ� �ٸ��� �ڽ��� ������ ��ȸ �����մϴ�. �޼��� ���. main.jsp
		 * ������ �̵�. 3-2 : id �Ķ���������� login ������ ���Ͽ� �ٸ����� login�� �������� ��� �̰ų� id�� �α��� ������
		 * ���� ��� ȭ�鿡 ���� ����ϱ� MemberDao().selectOne(id) �޼��带 �̿��Ͽ� db ���� ��ȸ�ϱ� 4. �����ڷ� �α���
		 * �� ��쳪, �ڽ����� ��ȸ�� ȭ�鿡 ����ϱ�
		 */
		String login = (String) session.getAttribute("login");

		String msg = "";
		String url = "";
		if (login == null || login.trim().equals("")) { // 2. logout ���
			msg = "�α����� �ʿ��մϴ�.�α��� �ϼ���";
			url = "loginForm";
			m.addAttribute("msg", msg);
			m.addAttribute("url", url);
			return "alert";
		} else if (!login.equals("admin") && !login.equals(id)) {
			msg = "�ڽ��� ������ ��ȸ�� �����մϴ�.";
			url = "loginForm";
			m.addAttribute("msg", msg);
			m.addAttribute("url", url);
			return "alert";
		} else { // login admin or id==login
			Member mem = dao.selectOne(id);
			m.addAttribute("mem", mem);
			return "member/memberInfo";
		}
	}

	@RequestMapping("updateForm")
	public String updateForm(HttpSession session, String id, Model m) {

		/*
		 * 1. id �Ķ���� 2. �α��λ��� ����. �α׾ƿ����� : �α����� �ʿ��մϴ�. �޼��� ����ϰ�, loginForm.jsp ������ �̵�
		 * 3. �α��� ���� 3-1 : id �Ķ���������� login ������ ���ؼ� �ٸ��� �ڽ��� ������ ���� �����մϴ�. �޼��� ���.
		 * info.jsp ������ �̵�. 3-2 : id �Ķ���������� login ������ ���Ͽ� �ٸ����� login�� �������� ��� �� id��
		 * �α��� ������ ���� ��� ȭ�鿡 ���� ����ϱ� MemberDao().selectOne(id) �޼��带 �̿��Ͽ� db ���� ��ȸ�ϱ� 4.
		 * id�� �ش��ϴ� ȸ���� ������ db���� ��ȸ ȭ�� ���
		 */
		String login = (String) session.getAttribute("login");

		String msg = "";
		String url = "";
		if (login == null || login.trim().equals("")) {
			msg = "�α����� �ʿ��մϴ�";
			url = "loginForm";
			m.addAttribute("msg", msg);
			m.addAttribute("url", url);
			return "alert";
		} else if (!login.equals("admin") && !login.equals(id)) {
			msg = "�ڽ��� ������ ������ �����մϴ�.";
			url = "loginForm";
			m.addAttribute("msg", msg);
			m.addAttribute("url", url);
			return "alert";
		} else {
			Member mem = dao.selectOne(id);
			m.addAttribute("mem", mem);

			return "member/updateForm";
		}
	}

	@RequestMapping("update")
	public String update(HttpSession session, Member mem, Model m) throws Throwable {

		/*
		 * 1. ��� �Ķ���͸� Member ��ü�� �����ϱ� 2. �Էµ� ��й�ȣ�� db�� ��й�ȣ�� ������ 3������ ����. �ٸ��� ��й�ȣ ����
		 * �޼��� ���. updateForm.jsp ������ �̵� 3. 1���� ��ü�� db�� �����ϱ�. int update(Member) �����
		 * 1�̻��̸� : ���� ���� �޼��� ���. main.jsp ������ �̵� 0���ϸ� : ���� ���� �޼��� ���. updateForm.jsp ������
		 * �̵�
		 */

		String login = (String) session.getAttribute("login");

		String msg = null;
		String url = null;

		Member dbMem = dao.selectOne(mem.getId());
		if (!login.equals("admin") && !mem.getPass().equals(dbMem.getPass())) {
			msg = "��й�ȣ�� Ʋ���ϴ�. Ȯ�� �� �ٽ� �ŷ� �ϼ���.";
			url = "updateForm?id=" + mem.getId();
		} else {
			if (dao.update(mem) > 0) {
				msg = mem.getId() + "���� ȸ�� ������ �����Ǿ����ϴ�.";
				url = "main";
			} else {
				msg = mem.getId() + "�� ȸ�� ���� ���� ����.";
				url = "updateForm?id=" + mem.getId();
			}
		}
		m.addAttribute("msg", msg);
		m.addAttribute("url", url);
		return "alert";
	}

	@RequestMapping("deleteForm")
	public String deleteForm(String id, Model m) throws Throwable {

		m.addAttribute("id", id);

		return "member/deleteForm";
	}

	@RequestMapping("delete")
	public String delete(HttpSession session, String id, String pass, Model m) throws Throwable {

		/*
		 * 1. �α׾ƿ����� : �α��� �ϼ���. �޼��� ��� �� loginForm.jsp ������ �̵� 2. �α��� ���� - �Ϲݻ���� (1) ��й�ȣ
		 * ���� (2) -��й�ȣ�� ��ġ�ϸ� db���� id �ش��ϴ� ���� ��������. �α׾ƿ� ��, 'Ż�� ����' �޽��� ���,
		 * loginForm.jsp ������ �̵� db���� id �ش��ϴ� ���� ��������. "���� ����" �޼��� ���. main.jsp ������ �̵�
		 * -��й�ȣ ����ġ "��й�ȣ ����ġ" �޼��� ���. deleteForm.jsp ������ �̵� - ������ (1) db���� �ش� id ���� ����
		 * db���� id �ش��ϴ� ���� ��������. "���� ����" �޼��� ���. list.jsp ������ �̵� (2) Ż�� ���� �޼��� ���.
		 * list.jsp ������ �̵�
		 */

		String login = (String) session.getAttribute("login");

		String msg = null;
		String url = null;
		if (login == null || login.trim().equals("")) {
			msg = "�α����� �ʿ��մϴ�.";
			url = "loginForm";
		} else if (!login.equals(id) && !login.equals("admin")) {
			msg = "���θ� Ż�� �����մϴ�.";
			url = "main";
		} else if (id.equals("admin")) {
			msg = "�����ڴ� Ż���� �� �����ϴ�.";
			url = "main";
		} else {

			Member mem = dao.selectOne(id);
			if (login.equals("admin") || pass.equals(mem.getPass())) {
				int result = dao.delete(id);
				if (result > 0) { // ���� ����
					if (login.equals("admin")) { // �������� ���
						msg = id + " ����ڸ� ���� Ż�� ����";
						url = "memberList";
					} else { // �Ϲݻ������ ���
						msg = id + "����  ȸ�� Ż�� �Ϸ�Ǿ����ϴ�.";
						url = "loginForm";
						session.invalidate();
					}
				} else { // ���� ����
					msg = id + "���� Ż��� ���� �߻�.";
					if (login.equals("admin")) { // �������� ���
						url = "memberList";
					} else { // �Ϲݻ������ ���
						url = "main";
					}
				}
			} else {
				msg = id + "���� �����ȣ�� Ʋ���ϴ�.";
				url = "deleteForm?id=" + id;
			}
		}
		m.addAttribute("url", url);
		m.addAttribute("msg", msg);
		return "alert";
	}

	@RequestMapping("memberList")
	public String memberList(HttpSession session, Model m) throws Throwable {
		/*
		 * 1. �α׾ƿ����� : �α��� �ϼ���. �޼��� ���. loginForm.jsp ������ �̵� 2. �α��λ��� �Ϲݻ���� �α���: �����ڸ�
		 * ��ȸ���� �޼��� ���. main.jsp ������ �̵� �����ڷα��� : ��� ��� 3. ȸ�� ����� ��ȸ�Ͽ� ȭ�鿡 ����ϱ�
		 */
		String msg = "";
		String url = "";
		String login = (String) session.getAttribute("login");
		if (login == null || login.trim().equals("")) {
			msg = "�����ڷ� �α��� �ϼ���";
			url = "loginForm";
			m.addAttribute("msg", msg);
			m.addAttribute("url", url);
			return "alert";
		} else if (!login.equals("admin")) {
			msg = "�����ڸ� ������ �ŷ� �Դϴ�.";
			url = "main";
			m.addAttribute("msg", msg);
			m.addAttribute("url", url);
			return "alert";

		} else { // �����ڸ� ��ȸ
			List<Member> list = dao.list();
			m.addAttribute("list", list);
			return "member/memberList";

		}

	}

	@RequestMapping("pictureimg")
	public String pictureimg(MultipartHttpServletRequest request, Model m) {

		String uploadpath = request.getServletContext().getRealPath("/memimg/"); // webapp/memimg folder
		MultipartFile multi = request.getFile("picture");
		String filename = "";
		if (!multi.isEmpty()) {
			File file = new File(uploadpath, multi.getOriginalFilename());
			filename = multi.getOriginalFilename();
			try {
				multi.transferTo(file);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		m.addAttribute("filename", filename);
		return "single/pictureimg";

	}

	@RequestMapping("password")
	public String password(HttpSession session, String pass, String chgpass, Model m) {

		String login = (String) session.getAttribute("login");
		boolean opener = false;
		String msg = null;
		String url = null;
		if (login == null || login.trim().equals("")) { // �α׾ƿ� ����
			opener = true;
			msg = "�α��� �ϼ���.";
			url = "loginForm";
		} else { // �α��� ����

			Member mem = dao.selectOne(login);
			if (pass.equals(mem.getPass())) {// �Էµ� ��й�ȣ�� db�� ����� ��й�ȣ�� ���� ���
				if (dao.updatePass(login, chgpass) > 0) {
					msg = "��й�ȣ�� ����Ǿ����ϴ�.";
					url = "memberInfo?id=" + login;
					opener = true;
				} else {
					msg = "��й�ȣ�� ����� ������ �߻� �Ǿ����ϴ�.";
					url = "passwordForm";
				}
			} else { // �Էµ� ��й�ȣ�� db�� ����� ��й�ȣ�� �ٸ� ���
				msg = "��й�ȣ ���� �Դϴ�. Ȯ���ϼ���.";
				url = "passwordForm";
			}
		}
		
		m.addAttribute("opener", opener);
		m.addAttribute("msg", msg);
		m.addAttribute("url", url);
		
		return "single/password";

	}

}
