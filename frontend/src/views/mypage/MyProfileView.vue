<template>
  <div class="pms-page is-narrow">
    <div class="page-header head-row">
      <div>
        <h1>내 정보</h1>
        <p>이름, 연락처 등 기본 정보를 관리합니다.</p>
      </div>
      <router-link :to="{ name: 'MyPage' }" class="btn btn-outline">← 마이페이지</router-link>
    </div>

    <div v-if="loading" class="card"><div class="empty-state">내 정보를 불러오는 중입니다.</div></div>

    <div v-else class="card">
      <div class="fcard-body">
        <!-- 프로필 이미지 (image_path) -->
        <div class="form-row">
          <label>프로필 이미지</label>
          <div class="image-row">
            <img v-if="previewUrl" :src="previewUrl" alt="프로필 이미지 미리보기" class="avatar-preview" />
            <span v-else class="avatar-preview avatar-fallback" aria-hidden="true">{{ initial }}</span>

            <div class="image-side">
              <div class="image-actions">
                <button type="button" class="btn btn-outline btn-sm" @click="$refs.fileInput.click()">이미지 선택</button>
                <input ref="fileInput" type="file" accept="image/*" class="file-input" tabindex="-1" aria-hidden="true" @change="onImageChange" />
                <button v-if="form.imagePath" type="button" class="btn btn-ghost btn-sm" @click="removeImage">삭제</button>
              </div>
              <input
                class="form-control"
                :value="form.imagePath"
                placeholder="image_path (이미지를 선택하면 경로가 채워집니다)"
                aria-label="image_path"
                readonly
              />
              <p v-if="errors.image" class="field-error">{{ errors.image }}</p>
              <p v-else class="field-hint">JPG, PNG 등 이미지 파일, 최대 2MB</p>
            </div>
          </div>
        </div>

        <div class="form-grid-2">
          <div class="form-row">
            <label for="mp-name">이름<span class="req">*</span></label>
            <input id="mp-name" v-model="form.name" class="form-control" maxlength="50" />
            <p v-if="errors.name" class="field-error">{{ errors.name }}</p>
          </div>
          <div class="form-row">
            <label for="mp-email">이메일<span class="req">*</span></label>
            <input id="mp-email" v-model="form.email" type="email" class="form-control" maxlength="100" />
            <p v-if="errors.email" class="field-error">{{ errors.email }}</p>
          </div>
        </div>

        <div class="form-grid-2">
          <div class="form-row">
            <label for="mp-phone">휴대폰</label>
            <input id="mp-phone" v-model="form.phone" class="form-control" placeholder="010-0000-0000" maxlength="13" inputmode="tel" />
            <p v-if="errors.phone" class="field-error">{{ errors.phone }}</p>
          </div>
          <div class="form-row">
            <label for="mp-birth">생년월일</label>
            <input id="mp-birth" v-model="form.birthDate" type="date" class="form-control" :max="todayText" />
            <p v-if="errors.birthDate" class="field-error">{{ errors.birthDate }}</p>
          </div>
        </div>

        <div class="form-grid-2">
          <div class="form-row">
            <label for="mp-job">직업</label>
            <input id="mp-job" v-model="form.job" class="form-control" maxlength="50" />
          </div>
          <div class="form-row">
            <label for="mp-nation">국적</label>
            <select id="mp-nation" v-model="form.nationality" class="form-control">
              <option v-for="n in nationalities" :key="n.code" :value="n.code">{{ n.label }}</option>
            </select>
          </div>
        </div>
      </div>

      <div class="fcard-footer">
        <router-link :to="{ name: 'MyPage' }" class="btn btn-outline">취소</router-link>
        <button type="button" class="btn btn-primary" :disabled="submitting" @click="onSubmit">
          {{ submitting ? '저장 중...' : '저장' }}
        </button>
      </div>
    </div>

    <AppToast :message="toastMessage" />
  </div>
</template>

<script>
import dayjs from 'dayjs'
import AppToast from '@/components/common/AppToast.vue'
import sampleViewMixin from '@/mixins/sampleViewMixin'
import { fetchMyInfo, saveMyInfo } from '@/services/myPageService'
import { NATIONALITIES } from '@/utils/myPageMeta'

const MAX_IMAGE_BYTES = 2 * 1024 * 1024
const PHONE_PATTERN = /^01[016789]-?\d{3,4}-?\d{4}$/
const EMAIL_PATTERN = /^\S+@\S+\.\S+$/

export default {
  name: 'MyProfileView',
  components: { AppToast },
  mixins: [sampleViewMixin],
  data() {
    return {
      nationalities: NATIONALITIES,
      todayText: dayjs().format('YYYY-MM-DD'),
      loading: true,
      submitting: false,
      form: { name: '', email: '', phone: '', birthDate: '', job: '', nationality: 'KR', imagePath: '' },
      previewUrl: '',
      pendingUrl: '', // 아직 저장하지 않은 미리보기 URL (교체/이탈 시 해제)
      errors: {},
    }
  },
  computed: {
    initial() {
      return (this.form.name || '?').charAt(0).toUpperCase()
    },
  },
  async created() {
    try {
      const { imageUrl, ...info } = await fetchMyInfo()
      this.form = { ...this.form, ...info }
      this.previewUrl = imageUrl || ''
    } catch (err) {
      // TODO: 공통 에러 처리(토스트/알림) 컨벤션에 맞춰 연결
      console.error('내 정보 조회 실패', err)
    } finally {
      this.loading = false
    }
  },
  beforeUnmount() {
    this.releasePending()
  },
  methods: {
    releasePending() {
      if (this.pendingUrl) {
        URL.revokeObjectURL(this.pendingUrl)
        this.pendingUrl = ''
      }
    },
    onImageChange(event) {
      const file = event.target.files && event.target.files[0]
      event.target.value = '' // 같은 파일을 다시 선택해도 change 가 발생하도록 초기화
      if (!file) return
      if (!file.type.startsWith('image/')) {
        this.errors = { ...this.errors, image: '이미지 파일만 선택할 수 있습니다.' }
        return
      }
      if (file.size > MAX_IMAGE_BYTES) {
        this.errors = { ...this.errors, image: '이미지는 2MB 이하만 등록할 수 있습니다.' }
        return
      }
      this.releasePending()
      this.pendingUrl = URL.createObjectURL(file)
      this.previewUrl = this.pendingUrl
      // TODO(API 연동): 파일 업로드 API 로 올리고 응답으로 받은 image_path 를 사용
      this.form.imagePath = `/upload/profile/${file.name}`
      this.errors = { ...this.errors, image: '' }
    },
    removeImage() {
      this.releasePending()
      this.previewUrl = ''
      this.form.imagePath = ''
    },
    validate() {
      const e = {}
      if (!this.form.name.trim()) e.name = '이름을 입력해주세요.'
      if (!EMAIL_PATTERN.test(this.form.email.trim())) e.email = '올바른 이메일 형식이 아닙니다.'
      if (this.form.phone && !PHONE_PATTERN.test(this.form.phone.trim())) e.phone = '올바른 휴대폰 번호 형식이 아닙니다. (예: 010-1234-5678)'
      if (this.form.birthDate && this.form.birthDate > this.todayText) e.birthDate = '생년월일은 오늘 이전 날짜여야 합니다.'
      this.errors = { ...e, image: this.errors.image }
      return Object.keys(e).length === 0
    },
    async onSubmit() {
      if (!this.validate() || this.submitting) return
      this.submitting = true
      try {
        await saveMyInfo({
          ...this.form,
          name: this.form.name.trim(),
          email: this.form.email.trim(),
          phone: this.form.phone.trim(),
          imageUrl: this.previewUrl,
        })
        this.pendingUrl = '' // 저장된 미리보기는 세션 동안 유지하므로 해제하지 않는다
        this.showToast('내 정보가 저장되었습니다.')
      } catch (err) {
        // TODO: 공통 에러 처리(토스트/알림) 컨벤션에 맞춰 연결
        console.error('내 정보 저장 실패', err)
      } finally {
        this.submitting = false
      }
    },
  },
}
</script>

<style scoped>
.image-row {
  display: flex;
  align-items: flex-start;
  gap: var(--space-4);
}

.avatar-preview {
  flex-shrink: 0;
  width: 72px;
  height: 72px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--color-primary-600) 0%, var(--color-accent-500) 100%);
  font-size: 24px;
  font-weight: 700;
  color: #fff;
}

.image-side {
  flex: 1;
  min-width: 0;
}

.image-actions {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  margin-bottom: var(--space-2);
}

/* 실제 파일 입력은 숨기고, 위의 '이미지 선택' 버튼이 대신 연다 */
.file-input {
  display: none;
}
</style>
