package com.example.demo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.service.RsaKeyService;
import com.example.demo.util.ChunkIdUtil;

@Service
public class RsaKeyServiceImpl implements RsaKeyService {

	@Autowired
	ChunkIdUtil chunkIdUtil;

	@Override
	public List<String> getKeyChunkIds() {

		return chunkIdUtil.getChunkIds();
	}

}
