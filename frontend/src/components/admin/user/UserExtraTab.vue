<template>
  <div class="card">
    <div class="fcard-body">
      <div class="form-row">
        <label for="x-image">프로필 이미지 (image_path)</label>
        <input id="x-image" class="form-control" :value="form.imagePath || ''" placeholder="등록된 이미지가 없습니다." readonly />
        <p class="field-hint">이미지는 사용자가 마이페이지에서 직접 변경합니다. 관리자는 경로만 확인할 수 있습니다.</p>
      </div>
      <div class="form-grid-2">
        <div class="form-row"><label for="x-birth">생년월일</label><input id="x-birth" v-model="form.birthDt" type="date" class="form-control" /></div>
        <div class="form-row">
          <label for="x-job">직업</label>
          <select id="x-job" v-model="form.jobCd" class="form-control"><option v-for="j in jobs" :key="j" :value="j">{{ j }}</option></select>
        </div>
      </div>
      <div class="form-row">
        <label for="x-country">국적</label>
        <select id="x-country" v-model="form.countryCd" class="form-control"><option v-for="c in countries" :key="c.code" :value="c.code">{{ c.label }}</option></select>
        <p class="field-hint">직업·국적은 공통코드(cfg_system_code) 값을 사용합니다.</p>
      </div>
    </div>
    <div class="fcard-footer"><button type="button" class="btn btn-primary" @click="onSave">저장</button></div>
  </div>
</template>

<script>
import adminStore from '@/store/adminStore'
import { JOBS, COUNTRIES } from '@/utils/adminMeta'

export default {
  name: 'UserExtraTab',
  props: { user: { type: Object, required: true } },
  emits: ['toast', 'error'],
  data() { return { jobs: JOBS, countries: COUNTRIES, form: { ...this.user.extra } } },
  methods: {
    async onSave() {
      try {
        await adminStore.updateUserExtra(this.user.userId, this.form)
        this.$emit('toast', '부가정보를 저장했습니다.')
      } catch (err) { this.$emit('error', err) }
    },
  },
}
</script>
